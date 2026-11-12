package org.example.ucb.dao;

import org.example.ucb.model.Veterinario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioDeVeterinarioSQLTest {

    private RepositorioDeVeterinarioSQL repositorio;

    @BeforeEach
    void prepararBanco() {
        repositorio = new RepositorioDeVeterinarioSQL(new BancoDeTesteH2());
    }

    @Test
    @DisplayName("salva um veterinário e encontra pelo CRMV")
    void salvaEBuscaPorCrmv() {
        repositorio.salvar(DadosDeTeste.veterinario("CRMV-1", "Dra. Bia"));

        Veterinario encontrado = repositorio.BuscarVet("CRMV-1");

        assertEquals("Dra. Bia", encontrado.getNome());
        assertEquals(40, encontrado.getIdade());
        assertEquals(LocalDate.of(2010, 1, 15), encontrado.getDataGraduacao());
    }

    @Test
    @DisplayName("lista todos os veterinários cadastrados")
    void listaVeterinarios() {
        repositorio.salvar(DadosDeTeste.veterinario("CRMV-1", "Dra. Bia"));
        repositorio.salvar(DadosDeTeste.veterinario("CRMV-2", "Dr. Caio"));

        assertEquals(2, repositorio.ListarVet().size());
    }

    @Test
    @DisplayName("atualiza nome e idade de um veterinário")
    void atualizaVeterinario() {
        repositorio.salvar(DadosDeTeste.veterinario("CRMV-1", "Dra. Bia"));

        repositorio.atualizar(new Veterinario("CRMV-1", "Dra. Beatriz", 41, LocalDate.of(2010, 1, 15)));

        Veterinario encontrado = repositorio.BuscarVet("CRMV-1");
        assertEquals("Dra. Beatriz", encontrado.getNome());
        assertEquals(41, encontrado.getIdade());
    }

    @Test
    @DisplayName("deleta um veterinário existente e devolve false na segunda vez")
    void deletaVeterinario() {
        repositorio.salvar(DadosDeTeste.veterinario("CRMV-1", "Dra. Bia"));

        assertTrue(repositorio.deletarVet("CRMV-1"));
        assertNull(repositorio.BuscarVet("CRMV-1"));
        assertFalse(repositorio.deletarVet("CRMV-1"));
    }
}
