package org.example.ucb.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConexaoMySQLTest {

    @Test
    @DisplayName("sem variáveis de ambiente, usa a URL e o usuário padrão do README")
    void usaValoresPadrao() {
        ConexaoMySQL conexao = new ConexaoMySQL(Map.<String, String>of()::get);

        assertEquals("jdbc:mysql://localhost:3306/clinica", conexao.url());
        assertEquals("root", conexao.usuario());
    }

    @Test
    @DisplayName("lê URL e usuário das variáveis CLINICA_DB_URL e CLINICA_DB_USER")
    void leVariaveisDeAmbiente() {
        Map<String, String> ambiente = Map.of(
                "CLINICA_DB_URL", "jdbc:mysql://db:3306/clinica_teste",
                "CLINICA_DB_USER", "clinica_app");
        ConexaoMySQL conexao = new ConexaoMySQL(ambiente::get);

        assertEquals("jdbc:mysql://db:3306/clinica_teste", conexao.url());
        assertEquals("clinica_app", conexao.usuario());
    }

    @Test
    @DisplayName("sem CLINICA_DB_PASSWORD, falha com mensagem que diz o que configurar")
    void exigeSenhaNoAmbiente() {
        ConexaoMySQL conexao = new ConexaoMySQL(Map.<String, String>of()::get);

        Exception erro = assertThrows(IllegalStateException.class, conexao::obterConexao);
        assertTrue(erro.getMessage().contains("CLINICA_DB_PASSWORD"));
    }
}
