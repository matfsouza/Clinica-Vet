-- =====================================================================
-- Clínica Veterinária — script de criação do banco de dados (MySQL 8)
--
-- O repositório não versionava nenhum script de banco. Este arquivo foi
-- reconstruído a partir das instruções SQL presentes nas classes
-- org.example.ucb.dao.*SQL (baseline: commit b36940e) e deve ser revisado
-- sempre que uma consulta SQL do código for alterada.
--
-- Observação sobre nomes de tabelas: o código mistura maiúsculas e
-- minúsculas (ex.: "Veterinario" e "veterinario"). Por isso o servidor
-- precisa usar lower_case_table_names=1 (padrão do MySQL no Windows).
--
-- Uso: mysql -u root -p < docs/banco/schema.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS clinica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE clinica;

-- Proprietário dos animais (RepositorioDeDonoSQL)
CREATE TABLE IF NOT EXISTS dono (
    CPF        VARCHAR(14)  NOT NULL,
    Nome       VARCHAR(100) NOT NULL,
    Endereco   VARCHAR(200),
    data_nasc  DATE         NOT NULL,  -- o DAO chama toLocalDate() sem checar null
    PRIMARY KEY (CPF)
);

-- Veterinário (RepositorioDeVeterinarioSQL)
CREATE TABLE IF NOT EXISTS veterinario (
    CRMV            VARCHAR(20)  NOT NULL,
    nome            VARCHAR(100) NOT NULL,
    idade           INT,
    data_Graduacao  DATE         NOT NULL,  -- o DAO chama toLocalDate() sem checar null
    PRIMARY KEY (CRMV)
);

-- Especialidade veterinária (RepositorioDeEspecialidadeSQL)
CREATE TABLE IF NOT EXISTS especialidade (
    IDespecialidade  INT          NOT NULL AUTO_INCREMENT,
    nome             VARCHAR(100) NOT NULL,
    PRIMARY KEY (IDespecialidade)
);

-- Certificação de um veterinário em uma especialidade (RepositorioDeCertificacaoSQL)
CREATE TABLE IF NOT EXISTS certificacao (
    NumeroRegistro            VARCHAR(30)  NOT NULL,
    DataObtencao              DATE         NOT NULL,
    InstituicaoCertificadora  VARCHAR(150),
    CRMV_certif               VARCHAR(20)  NOT NULL,
    ID_especialidade          INT          NOT NULL,
    PRIMARY KEY (NumeroRegistro),
    FOREIGN KEY (CRMV_certif)      REFERENCES veterinario (CRMV),
    FOREIGN KEY (ID_especialidade) REFERENCES especialidade (IDespecialidade)
);

-- Animal (superclasse) e seus subtipos Pet e Exotico (RepositorioDeAnimalSQL)
CREATE TABLE IF NOT EXISTS animal (
    ID        INT          NOT NULL AUTO_INCREMENT,
    Nome      VARCHAR(100) NOT NULL,
    Especie   VARCHAR(50),
    Idade     INT,
    Porte     VARCHAR(20),
    CPF_Dono  VARCHAR(14)  NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (CPF_Dono) REFERENCES dono (CPF)
);

CREATE TABLE IF NOT EXISTS pet (
    animal_ID  INT         NOT NULL,
    RFID       VARCHAR(50),
    PRIMARY KEY (animal_ID),
    FOREIGN KEY (animal_ID) REFERENCES animal (ID)
);

CREATE TABLE IF NOT EXISTS exotico (
    animal_ID    INT         NOT NULL,
    Nota_Fiscal  VARCHAR(50),
    RFIDEX       VARCHAR(50),
    PRIMARY KEY (animal_ID),
    FOREIGN KEY (animal_ID) REFERENCES animal (ID)
);

-- Consulta (RepositorioDeConsultaSQL)
CREATE TABLE IF NOT EXISTS consulta (
    id                INT          NOT NULL AUTO_INCREMENT,
    diagnostico       TEXT,
    id_animal         INT          NOT NULL,
    CRMV_veterinario  VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_animal)        REFERENCES animal (ID),
    FOREIGN KEY (CRMV_veterinario) REFERENCES veterinario (CRMV)
);

-- Tratamento prescrito em uma consulta (RepositorioDeTratamentoSQL)
CREATE TABLE IF NOT EXISTS tratamento (
    id                    INT      NOT NULL AUTO_INCREMENT,
    descricao_tratamento  TEXT,
    antibiotico           BOOLEAN  NOT NULL DEFAULT FALSE,
    id_consulta           INT      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_consulta) REFERENCES consulta (id)
);
