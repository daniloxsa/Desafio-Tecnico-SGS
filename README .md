# SGS — Sistema de Gestão de Solicitações

## Sobre o projeto

O **SGS (Sistema de Gestão de Solicitações)** é uma aplicação web para registro, consulta e
acompanhamento de **solicitações de pagamento** realizadas pelas diferentes áreas de uma
organização.

O processo, antes manual, passa a ser controlado por um fluxo com estados bem definidos —
da criação até a finalização — garantindo **rastreabilidade**, **padronização** e **controle**
das transições (aprovação, rejeição e cancelamento) conforme as regras de negócio.

Principais capacidades:

- Cadastro de solicitações vinculando **solicitante** e **categoria**;
- Listagem de solicitações com **filtros dinâmicos** (status, período, categoria, faixa de valor);
- Alteração de **status** a partir da listagem, respeitando a máquina de estados;
- Detalhamento de uma solicitação com todos os seus relacionamentos;
- Cadastro e consulta de solicitantes, com validação de **CPF e CNPJ** por dígito verificador —
  incluindo o **novo CNPJ alfanumérico** (IN RFB nº 2.229/2024, vigência jul/2026);
- Consulta das solicitações de um solicitante específico.

## Tecnologias utilizadas

### Backend
- **Java 25**
- **Spring Boot 4.1.1**
  - Spring Web MVC (API REST)
  - Spring Data JPA / Hibernate
  - Spring Validation (Bean Validation / Jakarta Validation)
  - Spring Boot DevTools
- **Maven** (com Maven Wrapper — `mvnw`)
- **MySQL 8** (driver `mysql-connector-j`)

### Frontend
- **HTML5, CSS3 e JavaScript puro (Vanilla JS)** — sem framework
- `fetch` API para consumo da API REST

### Banco de dados
- **MySQL 8.x** (relacional, com chaves estrangeiras)
- Uso obrigatório de **SQL nativo (Native Query)** na listagem principal

### Ferramentas
- **Git** para versionamento
- IntelliJ IDEA / VS Code
- Live Server ou `python -m http.server` para servir o frontend

## Arquitetura

O backend segue **arquitetura em camadas**, com separação clara de responsabilidades:

```
Controller  ->  Service  ->  Repository  ->  Banco de dados
   (HTTP)      (regras de     (acesso a
               negócio)        dados / JPA)
```

| Camada | Responsabilidade |
|--------|------------------|
| **Controller** | Expõe os endpoints REST, recebe/valida os DTOs de entrada e devolve os DTOs de resposta. Não contém regra de negócio. |
| **Service** | Concentra as regras de negócio: máquina de estados de status, validação de CPF/CNPJ, verificação de existência de solicitante/categoria, montagem dos DTOs. |
| **Repository** | Interfaces Spring Data JPA. Contém a **Native Query** de listagem com filtros dinâmicos e os métodos derivados. |
| **Entities** | Mapeamento objeto-relacional (`@Entity`). |
| **DTOs** | `record`s imutáveis para request e response — a entidade nunca é exposta diretamente na API. |
| **Exceptions** | Exceções de negócio + `@ControllerAdvice` global que padroniza as respostas de erro (`StandardError`). |

Fluxo de uma requisição de listagem:

```
GET /solicitacoes?status=LIBERADO&categoriaId=2
        |
   SolicitacaoController      -> converte query params
        |
   SolicitacaoService         -> repassa filtros (null quando ausentes)
        |
   SolicitacaoRepository      -> Native Query com JOIN + filtros dinâmicos
        |
   SolicitacaoProjection[]    -> projeção da query
        |
   List<SolicitacaoResponseDTO>  -> resposta JSON
```

### Padrão de projeto do frontend

Duas telas independentes, cada uma com seu script:

- `pages/index.html` + `script/app.js` — solicitações (listagem, filtros, criação, detalhes, alteração de status);
- `pages/solicitantes.html` + `script/solicitantes.js` — solicitantes (listagem, criação, solicitações do solicitante).

## Funcionalidades

| # | Funcionalidade | Onde |
|---|----------------|------|
| 1 | Cadastrar solicitação vinculando solicitante e categoria | Tela **Solicitações** → botão flutuante **+** |
| 2 | Listar solicitações com nome e documento do solicitante, categoria, status e valor | Tela **Solicitações** |
| 3 | Filtrar solicitações por **status**, **período** (data início/fim), **categoria** e faixa de **valor** | Tela **Solicitações** → barra de filtros |
| 4 | Alterar o status de uma solicitação a partir da listagem (respeitando as transições válidas) | Clique no *badge* de status na linha |
| 5 | Detalhar uma solicitação (CPF/CNPJ do solicitante, descrição e demais dados) | Botão **Ver mais** na linha |
| 6 | Cadastrar solicitante com validação de CPF/CNPJ | Tela **Solicitantes** → botão flutuante **+** |
| 7 | Listar solicitantes | Tela **Solicitantes** |
| 8 | Consultar as solicitações de um solicitante | Tela **Solicitantes** → **Ver mais** |
| 9 | Excluir solicitante | Endpoint `DELETE /solicitantes/{id}` |

## Modelagem do banco de dados

### Entidades

**`solicitante`** — pessoa ou unidade responsável pela solicitação
| Coluna | Tipo | Restrições |
|--------|------|------------|
| `id` | BIGINT | PK, AUTO_INCREMENT |
| `nome` | VARCHAR(100) | NOT NULL |
| `cpf_cnpj` | VARCHAR(24) | NOT NULL, **UNIQUE** |

**`categoria`** — tipo da solicitação de pagamento
| Coluna | Tipo | Restrições |
|--------|------|------------|
| `id` | BIGINT | PK, AUTO_INCREMENT |
| `nome` | VARCHAR(100) | NOT NULL, **UNIQUE** |

**`solicitacao`** — o pedido de pagamento
| Coluna | Tipo | Restrições |
|--------|------|------------|
| `id` | BIGINT | PK, AUTO_INCREMENT |
| `descricao` | VARCHAR(200) | NULL |
| `valor` | DECIMAL(10,2) | NOT NULL |
| `data_solicitacao` | DATE | NOT NULL |
| `status_solicitacao` | ENUM(`SOLICITADO`,`LIBERADO`,`APROVADO`,`REJEITADO`,`CANCELADO`) | NOT NULL, default `SOLICITADO` |
| `id_solicitante` | BIGINT | NOT NULL, **FK** → `solicitante(id)` |
| `id_categoria` | BIGINT | NOT NULL, **FK** → `categoria(id)` |

### Relacionamentos

- `solicitante (1) ──< (N) solicitacao` — um solicitante pode ter várias solicitações;
- `categoria (1) ──< (N) solicitacao` — uma categoria pode estar associada a várias solicitações.

### Diagrama (ER)

```
+---------------+          +------------------------+          +---------------+
|  solicitante  |          |      solicitacao       |          |   categoria   |
+---------------+          +------------------------+          +---------------+
| id (PK)       |1        *| id (PK)                |*        1| id (PK)       |
| nome          |----------| id_solicitante (FK)    |----------| nome (UQ)     |
| cpf_cnpj (UQ) |          | id_categoria   (FK)    |          +---------------+
+---------------+          | descricao              |
                          | valor                  |
                          | data_solicitacao       |
                          | status_solicitacao     |
                          +------------------------+
```

## Regras de negócio

1. Um **solicitante** pode ter **várias solicitações** (1:N).
2. Uma **categoria** pode estar associada a **várias solicitações** (1:N).
3. Toda solicitação **inicia obrigatoriamente com status `SOLICITADO`** (default na entidade e no banco e backend).
4. A **data da solicitação** é atribuída pelo backend no momento da criação (`LocalDate.now()`).
5. As **transições de status são validadas no backend e frontend** (`SolicitacaoService.validarTransicao`).
   Transições permitidas:

   | Status atual | Transições válidas |
   |--------------|--------------------|
   | `SOLICITADO` | `LIBERADO`, `REJEITADO` |
   | `LIBERADO` | `APROVADO`, `REJEITADO` |
   | `APROVADO` | `CANCELADO` |
   | `REJEITADO` | — (estado final) |
   | `CANCELADO` | — (estado final) |

   ```
   SOLICITADO ──> LIBERADO ──> APROVADO ──> CANCELADO
       │             │
       └──────┬──────┘
              v
          REJEITADO
   ```

   Qualquer transição fora dessas regras resulta em **HTTP 400** com a mensagem
   *"Violação na hierarquia de transição de status"*.
6. O **CPF/CNPJ** de um solicitante criado pela API é validado por **dígito verificador**
   (`DocumentoValidator`). Documento inválido resulta em **HTTP 400** e o campo é **único**.
   - **Normalização (no `SolicitanteService`):** antes de validar, o valor recebido é limpo com
     `replaceAll("[^a-zA-Z0-9]", "")` e convertido para **maiúsculas** — aceita máscara na
     entrada e persiste o documento sem pontuação. O mesmo valor normalizado é usado na
     verificação de duplicidade e na gravação.
   - **Dispatch por comprimento:** após a limpeza, **11 caracteres → CPF**, **14 → CNPJ**;
     qualquer outro comprimento é considerado inválido.
   - **CPF** — deve casar `\d{11}` (11 dígitos numéricos); os 2 DVs finais são conferidos pelo
     cálculo módulo 11. Sequências de dígito único são rejeitadas.
   - **CNPJ** — deve casar `[A-Z0-9]{14}`, com as **2 últimas posições obrigatoriamente
     numéricas** (DVs). As 12 posições de base podem ser:
     - **numéricas** (formato tradicional), ou
     - **alfanuméricas** `[0-9A-Z]` (**novo formato**, IN RFB nº 2.229/2024, vigência jul/2026).

     O valor de cada caractere da base é `ASCII − 48` (`'0'–'9'` → 0–9, `'A'–'Z'` → 17–42);
     o 1º DV é calculado sobre as 12 posições e o 2º sobre as 12 posições + 1º DV, ambos por
     módulo 11. O CNPJ 100% numérico é caso particular da mesma rotina. Sequências de
     caractere único são rejeitadas.
7. Não é permitido cadastrar dois solicitantes com o mesmo CPF/CNPJ (**HTTP 400**).
8. Na criação de uma solicitação, o **solicitante** e a **categoria** informados devem existir,
   caso contrário **HTTP 404**.
9. O `valor` da solicitação deve ser **positivo** e a `descricao` **não pode ser vazia**
   (Bean Validation).


## SQL Nativo

A **listagem principal de solicitações** utiliza obrigatoriamente **SQL nativo (Native Query)**,
com **JOIN** entre as três tabelas e **filtros dinâmicos** — cada filtro só é aplicado quando o
parâmetro correspondente é informado, usando o padrão `(:param IS NULL OR coluna <op> :param)`.

Arquivo: `Back-End/solicitacao-api/src/main/java/br/com/sergipetech/solicitacao_api/repositories/SolicitacaoRepository.java`

```sql
SELECT s.id,
       s.descricao,
       s.valor,
       s.data_solicitacao,
       s.status_solicitacao          AS status,
       so.id                         AS solicitante_id,
       so.nome                       AS solicitante_nome,
       so.cpf_cnpj                   AS solicitanteCpfCnpj,
       c.id                          AS categoria_id,
       c.nome                        AS categoria_nome
FROM solicitacao s
JOIN solicitante so ON so.id = s.id_solicitante
JOIN categoria   c  ON c.id  = s.id_categoria
WHERE (:status      IS NULL OR s.status_solicitacao = :status)
  AND (:categoriaId IS NULL OR s.id_categoria       = :categoriaId)
  AND (:dataInicio  IS NULL OR s.data_solicitacao  >= :dataInicio)
  AND (:dataFim     IS NULL OR s.data_solicitacao  <= :dataFim)
  AND (:valorMin    IS NULL OR s.valor             >= :valorMin)
  AND (:valorMax    IS NULL OR s.valor             <= :valorMax);
```

O resultado é mapeado por uma **projeção baseada em interface**(Obrigatorio no JPA) (`SolicitacaoProjection`),
evitando poluir a entidade `Solicitacao` com construtores/consultas específicas de relatório.

### Scripts DDL (criação das tabelas)

> Também disponível na pasta **`ScriptsSQL/..`**.

```sql
CREATE DATABASE IF NOT EXISTS `sgs`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

USE `sgs`;

-- Tabela `categoria`
CREATE TABLE IF NOT EXISTS `categoria` (
  `id`   BIGINT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categoria_nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela `solicitante`
CREATE TABLE IF NOT EXISTS `solicitante` (
  `id`       BIGINT NOT NULL AUTO_INCREMENT,
  `nome`     VARCHAR(100) NOT NULL,
  `cpf_cnpj` VARCHAR(24)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_solicitante_cpf_cnpj` (`cpf_cnpj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela `solicitacao`
CREATE TABLE IF NOT EXISTS `solicitacao` (
  `id`                 BIGINT NOT NULL AUTO_INCREMENT,
  `descricao`          VARCHAR(200) DEFAULT NULL,
  `valor`              DECIMAL(10,2) NOT NULL,
  `data_solicitacao`   DATE NOT NULL,
  `status_solicitacao` ENUM('SOLICITADO','LIBERADO','APROVADO','REJEITADO','CANCELADO')
                       NOT NULL DEFAULT 'SOLICITADO',
  `id_solicitante`     BIGINT NOT NULL,
  `id_categoria`       BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_solicitacao_solicitante_idx` (`id_solicitante`),
  KEY `fk_solicitacao_categoria_idx`   (`id_categoria`),
  CONSTRAINT `fk_solicitacao_categoria`
    FOREIGN KEY (`id_categoria`)   REFERENCES `categoria`   (`id`),
  CONSTRAINT `fk_solicitacao_solicitante`
    FOREIGN KEY (`id_solicitante`) REFERENCES `solicitante` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### Scripts DML (carga inicial — mínimo de 5 registros cada)

```sql
INSERT INTO categoria (nome) VALUES
('Suporte Técnico'),
('Reembolso de Despesas'),
('Manutenção de Equipamentos'),
('Infraestrutura'),
('Recursos Humanos');

INSERT INTO solicitante (nome, cpf_cnpj) VALUES
INSERT INTO solicitante (nome, cpf_cnpj) VALUES
('João da Silva',   '02889851095'),
('Maria Oliveira',  '32756298085'),
('Carlos Santos',   '02542363080'),
('Ana Souza',       '43947232055'),
('Pedro Almeida',   '75332031009'),
('Empresa ACME',   '06753863000116'),
('Empresa XPTO LTDA', '17422148000117'),
('Empresa Saturno SA', 'JTN734P0000189'),
('Empresa Marte LTDA', '3XVN6G1L000164');
```

## Como executar

### Pré-requisitos

| Ferramenta | Versão | Observação |
|------------|--------|------------|
| JDK | **25** | necessário para compilar/rodar o backend |
| Maven | 3.9+ | opcional — o projeto inclui o **Maven Wrapper** (`mvnw` / `mvnw.cmd`) |
| MySQL | **8.0+** | collation `utf8mb4_0900_ai_ci` |
| Navegador | atual | Chrome, Firefox, Edge… |
| Servidor estático | — | Live Server (VS Code) **ou** `python -m http.server` para servir o frontend |

### Banco de dados

1. Suba uma instância do MySQL 8 e conecte-se como um usuário com permissão de criação de schema.
2. Execute os arquivos que estão em **`ScriptsSQL/..`** (ou os blocos DDL/DML da seção
   [SQL Nativo](#sql-nativo)). Ele cria o schema `sgs`, as três tabelas, as *foreign keys* e a
   carga inicial de `categoria` e `solicitante`.

   ```bash
   mysql -u root -p < "Scripts de banco.sql"
   ```

3. Confirme/ajuste as credenciais em
   `Back-End/solicitacao-api/src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/sgs
   spring.datasource.username=root
   spring.datasource.password=1234

   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=true
   ```

   > `ddl-auto=validate`: o Hibernate **não cria nem altera** tabelas — apenas valida se o
   > schema existente bate com o mapeamento. Por isso o script SQL **deve ser executado antes**
   > de subir o backend.

### Backend

```bash
cd "Back-End/solicitacao-api"

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A API sobe em **http://localhost:8080**.

Para gerar o `.jar` executável:

```bash
./mvnw clean package
java -jar target/solicitacao-api-0.0.1-SNAPSHOT.jar
```

### Frontend

O frontend é estático (HTML/CSS/JS). O CORS já está liberado no backend (`@CrossOrigin`).

**Opção A — VS Code / Live Server**
1. Abra a pasta `Front-End/solicitacao-api-frontend`.
2. Clique com o botão direito em `pages/index.html` → **Open with Live Server**.

**Opção B — servidor HTTP simples**
```bash
cd "Front-End/solicitacao-api-frontend"
python -m http.server 5500
```
Acesse **http://localhost:5500/pages/index.html**.

> A URL base da API está definida na constante `API_URL` em `script/app.js` e
> `script/solicitantes.js` (`http://localhost:8080`). Ajuste se necessário.

## Endpoints da API

Base URL: `http://localhost:8080`

### Categorias
| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/categorias` | Lista todas as categorias |
| `GET` | `/categorias/{id}` | Busca uma categoria por id |

### Solicitantes
| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/solicitantes` | Lista todos os solicitantes |
| `GET` | `/solicitantes/{id}` | Busca um solicitante por id |
| `GET` | `/solicitantes/{id}/solicitacoes` | Lista as solicitações (resumo) de um solicitante |
| `POST` | `/solicitantes` | Cria um solicitante (valida CPF/CNPJ) |
| `DELETE` | `/solicitantes/{id}` | Remove um solicitante |

`POST /solicitantes` — corpo:
```json
{ "nome": "Fulano de Tal", "cpfCnpj": "529.982.247-25" }
```
O campo `cpfCnpj` aceita máscara e aceita CNPJ no **formato tradicional (numérico)** ou
**alfanumérico** (novo padrão). É validado por dígito verificador e normalizado (sem
pontuação, em maiúsculas) antes de ser gravado.

### Solicitações
| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/solicitacoes` | Lista solicitações (SQL nativo com JOIN + filtros) |
| `POST` | `/solicitacoes` | Cria uma solicitação (status inicial `SOLICITADO`) |
| `PATCH` | `/solicitacoes/{id}/status` | Altera o status respeitando as transições válidas |

`GET /solicitacoes` — *query params* (todos opcionais e combináveis):

| Parâmetro | Tipo | Exemplo |
|-----------|------|---------|
| `status` | enum | `LIBERADO` |
| `categoriaId` | long | `2` |
| `dataInicio` | date (ISO) | `2026-01-01` |
| `dataFim` | date (ISO) | `2026-12-31` |
| `valorMin` | decimal | `100.00` |
| `valorMax` | decimal | `5000.00` |

Exemplo: `GET /solicitacoes?status=SOLICITADO&categoriaId=1&dataInicio=2026-01-01`

`POST /solicitacoes` — corpo:
```json
{
  "solicitanteId": 1,
  "categoriaId": 2,
  "descricao": "Compra de material de escritório",
  "valor": 349.90
}
```

`PATCH /solicitacoes/{id}/status` — corpo:
```json
{ "statusSolicitacao": "LIBERADO" }
```

### Formato de erro (padrão)

Todo erro tratado retorna o objeto `StandardError`:
```json
{
  "timestamp": "2026-09-02T12:00:00Z",
  "status": 400,
  "error": "Erro de validação dos dados",
  "message": "valor: deve ser maior que 0; ",
  "path": "/solicitacoes"
}
```

| Situação | HTTP |
|----------|------|
| Recurso não encontrado (`ResourceNotFoundException`) | 404 |
| Transição de status inválida (`StatusTransactionError`) | 400 |
| CPF/CNPJ inválido (`InvalidDocumentException`) | 400 |
| Violação de integridade (ex.: CPF/CNPJ duplicado) | 400 |
| Falha de Bean Validation (`MethodArgumentNotValidException`) | 400 |

## Estrutura do projeto

```
.
├── README.md
├── Scripts de banco.sql                # DDL + DML
│
├── Back-End/
│   └── solicitacao-api/
│       ├── pom.xml
│       ├── mvnw  /  mvnw.cmd            # Maven Wrapper
│       └── src/
│           ├── main/
│           │   ├── java/br/com/sergipetech/solicitacao_api/
│           │   │   ├── SolicitacaoApiApplication.java
│           │   │   ├── controllers/    # CategoriaController, SolicitanteController, SolicitacaoController
│           │   │   ├── services/       # regras de negócio
│           │   │   │   ├── exception/  # ResourceNotFound, InvalidDocument, StatusTransactionError
│           │   │   │   └── utilities/  # DocumentoValidator (CPF, CNPJ numérico e alfanumérico)
│           │   │   ├── repositories/   # Spring Data JPA
│           │   │   │   └── queries/    # SolicitacaoProjection (projeção da Native Query)
│           │   │   ├── entities/       # Categoria, Solicitante, Solicitacao
│           │   │   ├── dto/            # records de request/response por domínio
│           │   │   ├── enums/          # StatusSolicitacao
│           │   │   └── exceptions/     # ResourceExceptionHandler (@ControllerAdvice), StandardError
│           │   └── resources/
│           │       └── application.properties
│           └── test/
│               └── java/.../SolicitacaoApiApplicationTests.java
│
└── Front-End/
    └── solicitacao-api-frontend/
        ├── pages/
        │   ├── index.html              # Solicitações: listagem, filtros, criação, detalhes, status
        │   └── solicitantes.html       # Solicitantes: listagem, criação, solicitações do solicitante
        ├── script/
        │   ├── app.js
        │   └── solicitantes.js
        └── assets/
            └── style.css
```

## Decisões técnicas

| Decisão | Justificativa |
|---------|---------------|
| **Arquitetura em camadas** (Controller / Service / Repository) | Requisito técnico e boa prática: isola HTTP, regra de negócio e persistência, facilitando teste e manutenção. |

| **DTOs como `record`** para request e response | Objetos imutáveis, concisos e sem *boilerplate*. A entidade JPA nunca é exposta na API, evitando *over-posting* e problemas de serialização de relacionamentos *lazy*. |

| **Native Query + projeção por interface** (`SolicitacaoProjection`) | Requisito obrigatório (SQL nativo com JOIN e filtros). A projeção mapeia o resultado sem contaminar a entidade nem exigir DTO com construtor posicional na query. |

| **Filtros dinâmicos via `(:param IS NULL OR ...)`** | Mantém **uma única** consulta SQL legível, sem recorrer a Criteria API, Specifications ou concatenação de strings (que abriria espaço para SQL injection). |

| **`spring.jpa.hibernate.ddl-auto=validate`** | O schema é versionado e controlado pelo script SQL entregue, não gerado pelo Hibernate. `validate` garante que o mapeamento e o banco estão coerentes na subida da aplicação. |

| **`StatusSolicitacao` como `ENUM` no banco e `EnumType.STRING` na entidade** | Legibilidade dos dados no banco e segurança contra reordenação dos valores do enum (o que quebraria `EnumType.ORDINAL`). |

| **Validação de CPF e CNPJ por dígito verificador, com suporte ao CNPJ alfanumérico** | Vai além de checar formato: garante documentos plausíveis. O CNPJ segue a IN RFB nº 2.229/2024 (base de 12 posições `[0-9A-Z]`, DVs numéricos calculados sobre o valor ASCII − 48 de cada caractere); o CNPJ tradicional puramente numérico é caso particular da mesma rotina. O documento é normalizado (máscara removida; CNPJ em maiúsculas) antes de persistir e mantido único. |

| **Tratamento de erros centralizado** (`@ControllerAdvice` + `StandardError`) | Respostas de erro previsíveis e uniformes para o frontend, com `timestamp`, `status`, `error`, `message` e `path`. |

| **`@CrossOrigin(origins = "*")`** | O frontend estático roda em outra origem/porta durante o desenvolvimento; libera o consumo da API sem proxy. |


## Autor

**Danilo Sá Almeida**

------------------
**Cronograma:** 24/09/2026 a 02/09/2026
