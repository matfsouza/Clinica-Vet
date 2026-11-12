package org.example.ucb.dao;

import org.example.ucb.model.Consulta;
import org.example.ucb.model.Tratamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioDeTratamentoSQLTest {

    private RepositorioDeTratamentoSQL repositorio;
    private Consulta consulta;

    @BeforeEach
    void prepararBanco() {
        BancoDeTesteH2 banco = new BancoDeTesteH2();
        repositorio = new RepositorioDeTratamentoSQL(banco);
        // A consulta entra por SQL direto: o DAO de consultas ainda está quebrado (#4).
        banco.executar("INSERT INTO dono VALUES ('111', 'Ana', 'Rua A', '1990-05-20')");
        banco.executar("INSERT INTO veterinario VALUES ('CRMV-1', 'Dra. Bia', 40, '2010-01-15')");
        banco.executar("INSERT INTO animal (Nome, Especie, Idade, Porte, CPF_Dono) VALUES ('Rex', 'Cão', 3, 'Médio', '111')");
        banco.executar("INSERT INTO consulta (diagnostico, id_animal, CRMV_veterinario) VALUES ('Otite', 1, 'CRMV-1')");
        consulta = new Consulta();
        consulta.setid(1);
    }

    @Test
    @DisplayName("salva um tratamento, recebe o id gerado e encontra pelo id")
    void salvaEBusca() {
        Tratamento t = new Tratamento(0, "Amoxicilina 7 dias", true, consulta);

        repositorio.salvar(t);
        Tratamento encontrado = repositorio.BuscarTratamento(t.getId());

        assertEquals(1, t.getId());
        assertEquals("Amoxicilina 7 dias", encontrado.getDescricao());
        assertTrue(encontrado.isAntibiotico());
        assertEquals(1, encontrado.getConsulta().getid());
    }

    @Test
    @DisplayName("lista todos os tratamentos e filtra pelos de uma consulta")
    void listaEFiltraPorConsulta() {
        repositorio.salvar(new Tratamento(0, "Amoxicilina", true, consulta));
        repositorio.salvar(new Tratamento(0, "Limpeza do ouvido", false, consulta));

        List<Tratamento> todos = repositorio.ListarTratamento();
        List<Tratamento> daConsulta = repositorio.BuscarPorConsulta(1);

        assertEquals(2, todos.size());
        assertEquals(2, daConsulta.size());
        assertTrue(repositorio.BuscarPorConsulta(99).isEmpty());
    }

    @Test
    @DisplayName("atualiza descrição e uso de antibiótico")
    void atualiza() {
        Tratamento t = new Tratamento(0, "Amoxicilina", true, consulta);
        repositorio.salvar(t);
        t.setDescricao("Pomada");
        t.setAntibiotico(false);

        repositorio.atualizarTratamento(t);

        Tratamento encontrado = repositorio.BuscarTratamento(t.getId());
        assertEquals("Pomada", encontrado.getDescricao());
        assertFalse(encontrado.isAntibiotico());
    }

    @Test
    @DisplayName("deleta um tratamento e devolve false na segunda vez")
    void deleta() {
        Tratamento t = new Tratamento(0, "Amoxicilina", true, consulta);
        repositorio.salvar(t);

        assertTrue(repositorio.deletarTratamento(t.getId()));
        assertNull(repositorio.BuscarTratamento(t.getId()));
        assertFalse(repositorio.deletarTratamento(t.getId()));
    }
}
