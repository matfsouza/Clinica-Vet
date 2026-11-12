package org.example.ucb.dao;

import org.example.ucb.model.Dono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioDeDonoSQLTest {

    private RepositorioDeDonoSQL repositorio;

    @BeforeEach
    void prepararBanco() {
        repositorio = new RepositorioDeDonoSQL(new BancoDeTesteH2());
    }

    @Test
    @DisplayName("salva um dono e encontra pelo CPF com todos os campos")
    void salvaEBuscaPorCpf() {
        repositorio.salvar(DadosDeTeste.dono("111", "Ana"));

        Dono encontrado = repositorio.BuscarPorCPF("111");

        assertEquals("Ana", encontrado.getNome());
        assertEquals("Rua A, 10", encontrado.getEndereco());
        assertEquals(LocalDate.of(1990, 5, 20), encontrado.getDataNascimento());
    }

    @Test
    @DisplayName("busca por CPF inexistente devolve null")
    void buscaInexistenteDevolveNull() {
        assertNull(repositorio.BuscarPorCPF("999"));
    }

    @Test
    @DisplayName("lista todos os donos cadastrados")
    void listaDonos() {
        repositorio.salvar(DadosDeTeste.dono("111", "Ana"));
        repositorio.salvar(DadosDeTeste.dono("222", "Bruno"));

        assertEquals(2, repositorio.ListarDono().size());
    }

    @Test
    @DisplayName("atualiza nome e endereço de um dono existente")
    void atualizaDono() {
        repositorio.salvar(DadosDeTeste.dono("111", "Ana"));
        Dono alterado = new Dono("111", LocalDate.of(1991, 1, 1), "Rua B, 20", "Ana Maria");

        repositorio.atualizar(alterado);

        Dono encontrado = repositorio.BuscarPorCPF("111");
        assertEquals("Ana Maria", encontrado.getNome());
        assertEquals("Rua B, 20", encontrado.getEndereco());
        assertEquals(LocalDate.of(1991, 1, 1), encontrado.getDataNascimento());
    }

    @Test
    @DisplayName("deleta um dono existente e devolve false para CPF inexistente")
    void deletaDono() {
        repositorio.salvar(DadosDeTeste.dono("111", "Ana"));

        assertTrue(repositorio.deletarDono("111"));
        assertNull(repositorio.BuscarPorCPF("111"));
        assertFalse(repositorio.deletarDono("111"));
    }
}
