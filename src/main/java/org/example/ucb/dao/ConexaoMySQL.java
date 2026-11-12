package org.example.ucb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

/**
 * Conexão com o MySQL configurada por variáveis de ambiente.
 *
 * <ul>
 *   <li>CLINICA_DB_URL (padrão: jdbc:mysql://localhost:3306/clinica)</li>
 *   <li>CLINICA_DB_USER (padrão: root)</li>
 *   <li>CLINICA_DB_PASSWORD (obrigatória, sem valor padrão)</li>
 * </ul>
 */
public class ConexaoMySQL implements ConexaoBD {

    static final String VAR_URL = "CLINICA_DB_URL";
    static final String VAR_USUARIO = "CLINICA_DB_USER";
    static final String VAR_SENHA = "CLINICA_DB_PASSWORD";

    private static final String URL_PADRAO = "jdbc:mysql://localhost:3306/clinica";
    private static final String USUARIO_PADRAO = "root";

    private final UnaryOperator<String> ambiente;

    public ConexaoMySQL() {
        this(System::getenv);
    }

    /** Permite que os testes troquem a fonte das variáveis de ambiente. */
    ConexaoMySQL(UnaryOperator<String> ambiente) {
        this.ambiente = ambiente;
    }

    String url() {
        return valorOuPadrao(VAR_URL, URL_PADRAO);
    }

    String usuario() {
        return valorOuPadrao(VAR_USUARIO, USUARIO_PADRAO);
    }

    @Override
    public Connection obterConexao() throws Exception {
        String senha = ambiente.apply(VAR_SENHA);
        if (senha == null) {
            throw new IllegalStateException("Defina a variável de ambiente " + VAR_SENHA
                    + " com a senha do banco (veja o README).");
        }
        try {
            Connection conexao = DriverManager.getConnection(url(), usuario(), senha);
            System.out.println("Conexao com o banco de dados estabelecida com sucesso!");
            return conexao;
        } catch (SQLException e) {
            throw new Exception("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    @Override
    public void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
                System.out.println("Conexao fechada com sucesso");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexao: " + e.getMessage());
            }
        }
    }

    private String valorOuPadrao(String variavel, String padrao) {
        String valor = ambiente.apply(variavel);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
