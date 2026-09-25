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

- **JDK 24 ou superior**. O `pom.xml` define `maven.compiler.source/target = 24`; com um JDK mais antigo o Maven falha com `invalid target release: 24`.
- **Maven 3.9+**, ou o IntelliJ IDEA, que já traz o Maven embutido.
- **MySQL 8** em `localhost:3306`.

## Configuração do banco

1. Crie o banco e as tabelas:

   ```bash
   mysql -u root -p < docs/banco/schema.sql
   ```

2. Ajuste as credenciais em `src/main/java/org/example/ucb/dao/ConexaoMySQL.java` (`URL`, `USUARIO`, `SENHA`). A conexão padrão é `jdbc:mysql://localhost:3306/clinica` com o usuário `root`.

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

## Limitações conhecidas (baseline `v1.0-baseline`)

Levantadas no diagnóstico de qualidade. Serão tratadas na segunda parte do projeto.

- **Módulo de consultas quebrado:** as queries de `RepositorioDeConsultaSQL` usam a tabela `consulta.html` em vez de `consulta` (mudança introduzida no commit `6eadb4f`). Toda operação de consulta falha, e o cadastro de tratamentos, que depende de uma consulta existente, também fica bloqueado.
- **Sem testes automatizados:** não existe `src/test`, e a cobertura é 0%.
- **Credenciais do banco no código-fonte** (`ConexaoMySQL.java`).
- **Entrada inválida encerra o sistema:** digitar um valor não numérico em um menu lança `InputMismatchException`, que só é capturada no `main`.
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
