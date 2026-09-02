# SGS — Sistema de Gestão de Solicitações

Desafio Técnico para o cargo de **Programador de Sistemas de Computação** — SergipeTec.

Aplicação web para registro, consulta e acompanhamento de solicitações de pagamento, com fluxo de status controlado por regras de negócio no backend.

## Tecnologias

| Camada     | Tecnologia                                      |
|------------|-------------------------------------------------|
| Backend    | Java 25, Spring Boot 4.1, Spring Data JPA       |
| Frontend   | HTML, CSS e JavaScript (vanilla)                |
| Banco      | MySQL 8+                                        |
| Build      | Maven (wrapper incluído)                        |

## Estrutura do projeto

```
Desafio-Tecnico-SGS/
├── Back-End/solicitacao-api/     # API REST (Spring Boot)
├── Front-End/solicitacao-api-frontend/
│   ├── pages/                    # Telas HTML
│   ├── script/                   # Lógica JavaScript
│   └── assets/                   # Estilos CSS
└── database/
    ├── ddl.sql                   # Criação do banco e tabelas
    └── dml.sql                   # Dados iniciais
```

## Pré-requisitos

- **Java 25** (JDK)
- **MySQL 8+** em execução local
- Navegador web moderno
- *(Opcional)* Extensão **Live Server** no VS Code para servir o frontend

## Configuração do banco de dados

### 1. Criar o banco e as tabelas (DDL)

Execute o script abaixo no MySQL (ou rode o arquivo `database/ddl.sql`):

```sql
CREATE DATABASE IF NOT EXISTS sgs
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sgs;

CREATE TABLE solicitante (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    cpf_cnpj VARCHAR(18) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_solicitante_cpf_cnpj (cpf_cnpj)
);

CREATE TABLE categoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE solicitacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    data_solicitacao DATE NOT NULL,
    status_solicitacao VARCHAR(20) NOT NULL DEFAULT 'SOLICITADO',
    id_solicitante BIGINT NOT NULL,
    id_categoria BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_solicitacao_solicitante
        FOREIGN KEY (id_solicitante) REFERENCES solicitante (id),
    CONSTRAINT fk_solicitacao_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria (id)
);
```

### 2. Popular dados iniciais (DML)

Execute o script abaixo (ou rode o arquivo `database/dml.sql`):

```sql
USE sgs;

INSERT INTO solicitante (nome, cpf_cnpj) VALUES
('Ana Silva', '12345678901'),
('Tech Solutions Ltda', '12345678000190'),
('Carlos Mendes', '98765432100'),
('Prefeitura Municipal', '11222333000144'),
('Juliana Costa', '45678912345'),
('Inovação Digital ME', '99887766000155');

INSERT INTO categoria (nome) VALUES
('Serviços'),
('Material'),
('Transporte'),
('Manutenção'),
('Consultoria'),
('Equipamentos');
```

### 3. Credenciais do banco

A API está configurada em `Back-End/solicitacao-api/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sgs
spring.datasource.username=root
spring.datasource.password=1234
```

Ajuste usuário e senha conforme o seu ambiente local, se necessário.

> **Importante:** a propriedade `spring.jpa.hibernate.ddl-auto=validate` exige que as tabelas já existam no banco antes de subir a API. Execute os scripts DDL e DML antes de iniciar o backend.

## Executando o backend

No terminal, acesse a pasta da API e inicie a aplicação:

```bash
cd Back-End/solicitacao-api
./mvnw spring-boot:run
```

No Windows (PowerShell ou CMD):

```bash
cd Back-End\solicitacao-api
mvnw.cmd spring-boot:run
```

A API ficará disponível em **http://localhost:8080**.

## Executando o frontend

1. Com o backend em execução, abra a pasta `Front-End/solicitacao-api-frontend/pages/`.
2. Inicie um servidor HTTP local (recomendado) com a extensão **Live Server** do VS Code, apontando para `index.html`.

   Alternativa via Python:

   ```bash
   cd Front-End/solicitacao-api-frontend
   python -m http.server 5500
   ```

   Acesse: **http://localhost:5500/pages/index.html**

> O frontend consome a API em `http://localhost:8080`. Certifique-se de que o backend está rodando antes de usar a interface.

## Funcionalidades

- **Cadastro de solicitações** — vincula solicitante e categoria; status inicial `SOLICITADO`
- **Listagem com filtros** — por status, categoria, período (data início/fim) e faixa de valor
- **Atualização de status** — diretamente na listagem, respeitando as regras de transição
- **Detalhamento** — modal com todos os dados da solicitação e relacionamentos
- **Gestão de solicitantes** — tela auxiliar para cadastro e consulta (`solicitantes.html`)

## Regras de transição de status

| Status atual | Transições permitidas      |
|--------------|----------------------------|
| SOLICITADO   | LIBERADO, REJEITADO        |
| LIBERADO     | APROVADO, REJEITADO        |
| APROVADO     | CANCELADO                  |
| REJEITADO    | *(estado final)*           |
| CANCELADO    | *(estado final)*           |

As validações são aplicadas no **backend** (`SolicitacaoService`).

## Endpoints da API

| Método   | Rota                          | Descrição                                      |
|----------|-------------------------------|------------------------------------------------|
| `GET`    | `/solicitacoes`               | Lista solicitações (com filtros opcionais)     |
| `POST`   | `/solicitacoes`               | Cria uma nova solicitação                      |
| `PATCH`  | `/solicitacoes/{id}/status`   | Altera o status de uma solicitação             |
| `GET`    | `/solicitantes`               | Lista todos os solicitantes                    |
| `GET`    | `/solicitantes/{id}`          | Busca solicitante por ID                       |
| `POST`   | `/solicitantes`               | Cadastra um solicitante                        |
| `DELETE` | `/solicitantes/{id}`          | Remove um solicitante                          |
| `GET`    | `/categorias`                 | Lista todas as categorias                      |
| `GET`    | `/categorias/{id}`            | Busca categoria por ID                         |

### Filtros da listagem (`GET /solicitacoes`)

| Parâmetro     | Tipo          | Descrição                    |
|---------------|---------------|------------------------------|
| `status`      | String        | Status da solicitação        |
| `categoriaId` | Long          | ID da categoria              |
| `dataInicio`  | LocalDate     | Data inicial (yyyy-MM-dd)    |
| `dataFim`     | LocalDate     | Data final (yyyy-MM-dd)      |
| `valorMin`    | BigDecimal    | Valor mínimo                 |
| `valorMax`    | BigDecimal    | Valor máximo                 |

## Decisões técnicas

**Arquitetura em camadas (Controller → Service → Repository)**  
Separa responsabilidades e facilita manutenção, testes e evolução do código, conforme exigido pelo desafio.

**SQL nativo na listagem principal**  
A consulta de solicitações usa `@Query` com `nativeQuery = true`, realizando `JOIN` entre `solicitacao`, `solicitante` e `categoria`, com filtros dinâmicos via parâmetros opcionais (`IS NULL OR ...`). Isso atende ao requisito de avaliação de domínio em SQL.

**Frontend em HTML/CSS/JS puro**  
Escolhido por simplicidade e alinhamento com o escopo do desafio, evitando complexidade desnecessária de frameworks e mantendo foco na integração com a API.

**Scripts SQL para solicitantes e categorias**  
Em vez de telas obrigatórias de cadastro para essas entidades, os dados iniciais são fornecidos via DML (conforme permitido pelo edital). A API e o frontend ainda permitem cadastro de solicitantes quando necessário.

**`ddl-auto=validate`**  
Garante que o schema do banco corresponda às entidades JPA sem alterações automáticas, reforçando o controle explícito via scripts DDL versionados.

**DTOs e Bean Validation**  
Entrada e saída da API são tipadas com records/DTOs e validadas com Jakarta Validation, com tratamento centralizado de erros em `ResourceExceptionHandler`.

**CORS liberado (`@CrossOrigin`)**  
Permite que o frontend, servido em porta diferente da API, consuma os endpoints sem bloqueio do navegador.

## Repositório

https://github.com/daniloxsa/Desafio-Tecnico-SGS
