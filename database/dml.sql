-- Dados iniciais para solicitantes e categorias (mínimo de 5 registros cada)

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
