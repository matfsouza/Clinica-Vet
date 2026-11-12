# Clínica Veterinária — Sistema de Gestão (CLI)

Sistema de linha de comando para o dia a dia de uma clínica veterinária: cadastro de donos, veterinários, animais (pets e exóticos), consultas, tratamentos, especialidades e certificações. Os dados são gravados em um banco MySQL via JDBC.

Projeto desenvolvido na disciplina de Programação Orientada a Objetos (UCB, 2025/2) e reutilizado como objeto de análise na disciplina de Qualidade de Software (UCB, 2026/2).

## Tecnologias

| Item | Versão / detalhe |
|---|---|
| Linguagem | Java — o `pom.xml` compila com `release 24` |
| Build | Maven |
| Banco de dados | MySQL 8 |
| Driver | `mysql-connector-java` 8.0.28 |
| Persistência | JDBC puro (sem ORM) |
| Frontend | Protótipo HTML/JS estático, **não integrado** ao backend |

## Estrutura do projeto

```
src/main/java/org/example/ucb/
├── cli/        SistemaClinicaVet.java — ponto de entrada (main) e menus
├── control/    Interfaces dos repositórios (RepositorioDeAnimal, ...)
├── dao/        Conexão MySQL e implementações SQL dos repositórios
└── model/      Entidades: Animal, Pet, Exotico, Dono, Veterinario, Consulta,
                Tratamento, Especialidade, Certificacao
src/main/frontend/templates/index.html   Protótipo de interface web (dados em memória)
docs/banco/schema.sql                    Script de criação do banco
```

## Pré-requisitos

- **JDK 21 ou superior**. O `pom.xml` compila com `maven.compiler.release = 21` (LTS).
- **Maven 3.9+**, ou o IntelliJ IDEA, que já traz o Maven embutido.
- **MySQL 8** em `localhost:3306`.

## Configuração do banco

1. Crie o banco e as tabelas:

   ```bash
   mysql -u root -p < docs/banco/schema.sql
   ```

2. Defina as credenciais em variáveis de ambiente. O código não guarda nenhuma senha.

   | Variável | Obrigatória | Padrão |
   |---|---|---|
   | `CLINICA_DB_PASSWORD` | sim | (nenhum) |
   | `CLINICA_DB_USER` | não | `root` |
   | `CLINICA_DB_URL` | não | `jdbc:mysql://localhost:3306/clinica` |

   ```bash
   # Linux / macOS
   export CLINICA_DB_PASSWORD='sua-senha'
   ```

   ```powershell
   # Windows (PowerShell)
   $env:CLINICA_DB_PASSWORD = 'sua-senha'
   ```

   No IntelliJ, as variáveis ficam em *Run → Edit Configurations → Environment variables*. Sem `CLINICA_DB_PASSWORD`, o sistema avisa qual variável falta.

3. **Nomes de tabela e maiúsculas/minúsculas:** o código usa os mesmos nomes com grafias diferentes (por exemplo `Veterinario` e `veterinario`). No Windows isso funciona porque o MySQL já vem com `lower_case_table_names=1`. No Linux ou no macOS, o servidor precisa ser **inicializado** com `lower_case_table_names=1`. Sem isso, parte das consultas falha.

## Como executar

**IntelliJ IDEA:** abra a pasta do projeto, espere o Maven importar as dependências e execute a classe `org.example.ucb.cli.SistemaClinicaVet`.

**Linha de comando:**

```bash
mvn compile dependency:copy-dependencies
```

```bash
# Windows
java -cp "target/classes;target/dependency/*" org.example.ucb.cli.SistemaClinicaVet
```

```bash
# Linux / macOS
java -cp "target/classes:target/dependency/*" org.example.ucb.cli.SistemaClinicaVet
```

**Frontend:** abra `src/main/frontend/templates/index.html` no navegador. É um protótipo e os dados ficam só na memória da página.

## Testes

```bash
mvn verify
```

Os testes não precisam de MySQL. Cada classe de teste cria um banco H2 em memória, no modo MySQL, a partir do próprio `docs/banco/schema.sql`. O relatório de cobertura (JaCoCo) fica em `target/site/jacoco/index.html`.

O pipeline em `.github/workflows/ci.yml` roda o mesmo comando a cada push e pull request.

## Limitações conhecidas (versão `v2.0-parte2`)

A Parte 2 corrigiu o módulo de consultas, a atualização de animal exótico, a listagem de certificações e o encerramento da CLI com entrada não numérica (issue #4). O que continua em aberto:

- **`SistemaClinicaVet` concentra os menus:** 913 linhas de código e 6 métodos com complexidade ciclomática acima de 15 (máximo 36, em `exibirMenuAnimais`). Os menus não têm teste automatizado.
- **Sem validação de dados:** CPF, idade (aceita negativo) e porte são gravados como o usuário digita.
- **Cadastro de animal sem transação:** o animal e o subtipo (Pet ou Exotico) entram em dois INSERTs separados.
- **Frontend não integrado:** `index.html` não se comunica com o backend Java.

## Fluxo de contribuição

Toda mudança segue o fluxo abaixo:

1. Abrir uma **issue** descrevendo o problema ou a melhoria.
2. Criar uma **branch** a partir da `main` com o nome `tipo/descricao-curta` (`feat/`, `fix/`, `docs/`, `refactor/`, `test/`).
3. Fazer **commits** pequenos, com mensagem no formato `tipo: descrição`, referenciando a issue (`Refs #N`).
4. Abrir um **pull request** para a `main` com `Closes #N` na descrição.
5. Obter a **revisão** de outro integrante antes do merge. Não é permitido aprovar o próprio PR.
6. Fazer o **merge** e, quando a mudança fechar uma entrega, criar uma **tag** anotada (`vX.Y-descricao`).

## Autores

Desenvolvimento original (POO, 2025/2), conforme o histórico do Git: Victor Caldas Nery, Renan, Vitor Augusto Cunha de Sousa e pedromaiquen.
