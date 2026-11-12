package org.example.ucb.dao;

import org.example.ucb.model.Dono;
import org.example.ucb.model.Especialidade;
import org.example.ucb.model.Exotico;
import org.example.ucb.model.Pet;
import org.example.ucb.model.Veterinario;

import java.time.LocalDate;

/** Objetos de exemplo usados nos testes dos DAOs. */
final class DadosDeTeste {

    private DadosDeTeste() {
    }

    static Dono dono(String cpf, String nome) {
        return new Dono(cpf, LocalDate.of(1990, 5, 20), "Rua A, 10", nome);
    }

    static Veterinario veterinario(String crmv, String nome) {
        return new Veterinario(crmv, nome, 40, LocalDate.of(2010, 1, 15));
    }

    static Pet pet(String nome, Dono dono) {
        Pet pet = new Pet(0, nome, "Médio", 3, "Cão", "RFID-" + nome, dono);
        pet.setDono(dono);
        return pet;
    }

    static Exotico exotico(String nome, Dono dono) {
        Exotico exotico = new Exotico();
        exotico.setNome(nome);
        exotico.setPorte("Pequeno");
        exotico.setIdade(2);
        exotico.setEspecie("Iguana");
        exotico.setDono(dono);
        exotico.setRfidex("RFIDEX-" + nome);
        exotico.setNotaFiscal("NF-" + nome);
        return exotico;
    }

    static Especialidade especialidade(String nome) {
        Especialidade especialidade = new Especialidade();
        especialidade.setNome(nome);
        return especialidade;
    }
}
