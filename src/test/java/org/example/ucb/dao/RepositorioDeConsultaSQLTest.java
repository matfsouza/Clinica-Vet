package org.example.ucb.dao;

import org.example.ucb.model.Consulta;
import org.example.ucb.model.Pet;
import org.example.ucb.model.Veterinario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Uma verificação por operação do módulo de consultas (#4).
 * Na baseline todas falham, porque as queries usam a tabela "consulta.html".
 */
class RepositorioDeConsultaSQLTest {

    private RepositorioDeConsultaSQL repositorio;
    private Pet rex;
    private Veterinario bia;

    @BeforeEach
    void prepararBanco() {
        BancoDeTesteH2 banco = new BancoDeTesteH2();
        repositorio = new RepositorioDeConsultaSQL(banco);
        var ana = DadosDeTeste.dono("111", "Ana");
        new RepositorioDeDonoSQL(banco).salvar(ana);
        bia = DadosDeTeste.veterinario("CRMV-1", "Dra. Bia");
        new RepositorioDeVeterinarioSQL(banco).salvar(bia);
        rex = DadosDeTeste.pet("Rex", ana);
        new RepositorioDeAnimalSQL(banco).salvar(rex);
    }

    private Consulta novaConsulta(String diagnostico) {
        Consulta consulta = new Consulta(0, diagnostico, rex, bia);
        repositorio.salvar(consulta);
        return consulta;
    }

    @Test
    @DisplayName("salvar: grava a consulta e devolve o id gerado")
    void salvarGravaERecebeId() {
        Consulta consulta = novaConsulta("Otite");

        assertEquals(1, consulta.getid());
    }

    @Test
    @DisplayName("buscar por id: traz diagnóstico, animal e veterinário")
    void buscarPorId() {
        Consulta salva = novaConsulta("Otite");

        Consulta encontrada = repositorio.BuscarConsulta(salva.getid());

        assertNotNull(encontrada);
        assertEquals("Otite", encontrada.getdiagnostico());
        assertEquals("Rex", encontrada.getanimal().getNome());
        assertEquals("Dra. Bia", encontrada.getveterinario().getNome());
    }

    @Test
    @DisplayName("listar: traz todas as consultas")
    void listar() {
        novaConsulta("Otite");
        novaConsulta("Vacina V10");

        List<Consulta> todas = repositorio.ListarConsulta();

        assertEquals(2, todas.size());
    }

    @Test
    @DisplayName("buscar por animal: traz só as consultas daquele animal")
    void buscarPorAnimal() {
        novaConsulta("Otite");

        assertEquals(1, repositorio.BuscarPorAnimal(rex.getId()).size());
        assertTrue(repositorio.BuscarPorAnimal(99).isEmpty());
    }

    @Test
    @DisplayName("atualizar: grava o novo diagnóstico")
    void atualizar() {
        Consulta consulta = novaConsulta("Otite");
        consulta.setdiagnostico("Otite externa, retorno em 15 dias");

        repositorio.atualizarConsulta(consulta);

        assertEquals("Otite externa, retorno em 15 dias",
                repositorio.BuscarConsulta(consulta.getid()).getdiagnostico());
    }

    @Test
    @DisplayName("deletar: remove a consulta e devolve false na segunda vez")
    void deletar() {
        Consulta consulta = novaConsulta("Otite");

        assertTrue(repositorio.deletarConsulta(consulta.getid()));
        assertNull(repositorio.BuscarConsulta(consulta.getid()));
        assertFalse(repositorio.deletarConsulta(consulta.getid()));
    }
}
