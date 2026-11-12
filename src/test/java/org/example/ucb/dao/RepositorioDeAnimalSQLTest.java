package org.example.ucb.dao;

import org.example.ucb.model.Animal;
import org.example.ucb.model.Dono;
import org.example.ucb.model.Exotico;
import org.example.ucb.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioDeAnimalSQLTest {

    private RepositorioDeAnimalSQL repositorio;
    private Dono ana;

    @BeforeEach
    void prepararBanco() {
        BancoDeTesteH2 banco = new BancoDeTesteH2();
        repositorio = new RepositorioDeAnimalSQL(banco);
        ana = DadosDeTeste.dono("111", "Ana");
        new RepositorioDeDonoSQL(banco).salvar(ana);
    }

    @Test
    @DisplayName("salva um pet, recebe o id gerado e lê de volta como Pet com RFID")
    void salvaEBuscaPet() {
        Pet rex = DadosDeTeste.pet("Rex", ana);

        repositorio.salvar(rex);
        Animal encontrado = repositorio.BuscarPorId(rex.getId());

        assertEquals(1, rex.getId());
        Pet pet = assertInstanceOf(Pet.class, encontrado);
        assertEquals("Rex", pet.getNome());
        assertEquals("Cão", pet.getEspecie());
        assertEquals("RFID-Rex", pet.getrfid());
    }

    @Test
    @DisplayName("salva um exótico e lê de volta com nota fiscal e RFIDEX")
    void salvaEBuscaExotico() {
        Exotico iggy = DadosDeTeste.exotico("Iggy", ana);

        repositorio.salvar(iggy);

        Exotico encontrado = assertInstanceOf(Exotico.class, repositorio.BuscarPorId(iggy.getId()));
        assertEquals("NF-Iggy", encontrado.getNotaFiscal());
        assertEquals("RFIDEX-Iggy", encontrado.getRfidex());
    }

    @Test
    @DisplayName("lista todos os animais com o dono preenchido")
    void listaTodosComDono() {
        repositorio.salvar(DadosDeTeste.pet("Rex", ana));
        repositorio.salvar(DadosDeTeste.exotico("Iggy", ana));

        List<Animal> todos = repositorio.ListarTodos();

        assertEquals(2, todos.size());
        assertEquals("Ana", todos.get(0).getDono().getNome());
    }

    @Test
    @DisplayName("busca os animais de um dono pelo CPF")
    void buscaPorDono() {
        repositorio.salvar(DadosDeTeste.pet("Rex", ana));

        assertEquals(1, repositorio.BuscarPorDono("111").size());
        assertTrue(repositorio.BuscarPorDono("999").isEmpty());
    }

    @Test
    @DisplayName("atualiza nome, idade e RFID de um pet")
    void atualizaPet() {
        Pet rex = DadosDeTeste.pet("Rex", ana);
        repositorio.salvar(rex);
        rex.setNome("Rex II");
        rex.setIdade(4);
        rex.setrfid("RFID-NOVO");

        repositorio.atualizar(rex);

        Pet encontrado = (Pet) repositorio.BuscarPorId(rex.getId());
        assertEquals("Rex II", encontrado.getNome());
        assertEquals(4, encontrado.getIdade());
        assertEquals("RFID-NOVO", encontrado.getrfid());
    }

    @Test
    @DisplayName("deleta um pet junto com o registro do subtipo")
    void deletaPet() {
        Pet rex = DadosDeTeste.pet("Rex", ana);
        repositorio.salvar(rex);

        assertTrue(repositorio.deletarAnimal(rex.getId()));
        assertNull(repositorio.BuscarPorId(rex.getId()));
        assertFalse(repositorio.deletarAnimal(rex.getId()));
    }
}
