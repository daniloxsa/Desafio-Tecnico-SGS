-- Criação do banco e tabelas do SGS (Sistema de Gestão de Solicitações)

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
