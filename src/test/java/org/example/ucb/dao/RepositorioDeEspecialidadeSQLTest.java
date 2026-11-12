package org.example.ucb.dao;

import org.example.ucb.model.Especialidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioDeEspecialidadeSQLTest {

    private BancoDeTesteH2 banco;
    private RepositorioDeEspecialidadeSQL repositorio;

    @BeforeEach
    void prepararBanco() {
        banco = new BancoDeTesteH2();
        repositorio = new RepositorioDeEspecialidadeSQL(banco);
    }

    @Test
    @DisplayName("salva especialidades e lista com o id gerado pelo banco")
    void salvaELista() {
        repositorio.salvar(DadosDeTeste.especialidade("Dermatologia"));
        repositorio.salvar(DadosDeTeste.especialidade("Cardiologia"));

        List<Especialidade> todas = repositorio.ListarEspecialidade();

        assertEquals(2, todas.size());
        assertEquals("Dermatologia", todas.get(0).getNome());
        assertEquals(1, todas.get(0).getId());
    }

    @Test
    @DisplayName("busca uma especialidade pelo id e devolve null para id inexistente")
    void buscaPorId() {
        repositorio.salvar(DadosDeTeste.especialidade("Dermatologia"));

        assertEquals("Dermatologia", repositorio.BuscarEspecialidade(1).getNome());
        assertNull(repositorio.BuscarEspecialidade(99));
    }

    @Test
    @DisplayName("atualiza o nome de uma especialidade")
    void atualiza() {
        repositorio.salvar(DadosDeTeste.especialidade("Dermato"));

        repositorio.atualizarEspecialidade(new Especialidade(1, "Dermatologia"));

        assertEquals("Dermatologia", repositorio.BuscarEspecialidade(1).getNome());
    }

    @Test
    @DisplayName("deleta uma especialidade existente e devolve false na segunda vez")
    void deleta() {
        repositorio.salvar(DadosDeTeste.especialidade("Dermatologia"));

        assertTrue(repositorio.deletarEspecialidade(1));
        assertFalse(repositorio.deletarEspecialidade(1));
    }

    @Test
    @DisplayName("lista só as especialidades em que o veterinário é certificado")
    void buscaPorVeterinario() {
        repositorio.salvar(DadosDeTeste.especialidade("Dermatologia"));
        repositorio.salvar(DadosDeTeste.especialidade("Cardiologia"));
        banco.executar("INSERT INTO veterinario (CRMV, nome, idade, data_Graduacao) VALUES ('CRMV-1', 'Dra. Bia', 40, '2010-01-15')");
        banco.executar("INSERT INTO certificacao VALUES ('REG-1', '2015-03-01', 'CFMV', 'CRMV-1', 2)");

        List<Especialidade> doVet = repositorio.BuscarEspPorVet("CRMV-1");

        assertEquals(1, doVet.size());
        assertEquals("Cardiologia", doVet.get(0).getNome());
    }
}
