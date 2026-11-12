package org.example.ucb.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Banco H2 em memória, no modo MySQL, criado a partir de docs/banco/schema.sql.
 * Cada instância usa um banco novo, então os testes não dividem dados.
 */
public class BancoDeTesteH2 implements ConexaoBD {

    private static final Path SCHEMA = Path.of("docs", "banco", "schema.sql");

    private final String url;

    public BancoDeTesteH2() {
        this.url = "jdbc:h2:mem:clinica_" + UUID.randomUUID().toString().replace("-", "")
                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1";
        criarTabelas();
    }

    @Override
    public Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    @Override
    public void fecharConexao(Connection conexao) {
        try {
            if (conexao != null) {
                conexao.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Executa um comando SQL avulso, usado para preparar ou conferir dados nos testes. */
    public void executar(String sql) {
        try (Connection c = obterConexao(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao executar: " + sql, e);
        }
    }

    private void criarTabelas() {
        String script;
        try {
            script = Files.readString(SCHEMA, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler " + SCHEMA, e);
        }
        StringBuilder semComentarios = new StringBuilder();
        for (String linha : script.split("\\R")) {
            int comentario = linha.indexOf("--");
            semComentarios.append(comentario >= 0 ? linha.substring(0, comentario) : linha).append('\n');
        }
        try (Connection c = obterConexao(); Statement st = c.createStatement()) {
            for (String comando : semComentarios.toString().split(";")) {
                String sql = comando.trim();
                String inicio = sql.toUpperCase();
                // CREATE DATABASE e USE são próprios do MySQL; o H2 já abre o banco certo.
                if (sql.isEmpty() || inicio.startsWith("CREATE DATABASE") || inicio.startsWith("USE ")) {
                    continue;
                }
                st.execute(sql);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("schema.sql não executou no H2", e);
        }
    }
}
