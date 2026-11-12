package org.example.ucb.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeloTest {

    private final Dono dono = new Dono("123.456.789-00", LocalDate.of(1990, 5, 20), "Rua A, 10", "Ana");

    @Test
    @DisplayName("Dono: o construtor guarda cada valor no campo certo, apesar da ordem (CPF, data, endereço, nome)")
    void donoGuardaCamposNaOrdemDoConstrutor() {
        assertEquals("123.456.789-00", dono.getCPF());
        assertEquals(LocalDate.of(1990, 5, 20), dono.getDataNascimento());
        assertEquals("Rua A, 10", dono.getEndereco());
        assertEquals("Ana", dono.getNome());
    }

    @Test
    @DisplayName("Pet: o construtor completo preenche os dados herdados de Animal e o RFID")
    void petPreencheDadosDeAnimalERfid() {
        Pet pet = new Pet(7, "Rex", "Médio", 3, "Cão", "RFID-1", dono);
        assertInstanceOf(Animal.class, pet);
        assertEquals(7, pet.getId());
        assertEquals("Rex", pet.getNome());
        assertEquals("Médio", pet.getPorte());
        assertEquals(3, pet.getIdade());
        assertEquals("Cão", pet.getEspecie());
        assertEquals("RFID-1", pet.getrfid());
        assertSame(dono, pet.getDono());
    }

    @Test
    @DisplayName("Exotico: o construtor completo grava RFIDEX e nota fiscal (#4, java:S1656)")
    void exoticoGuardaRfidexENotaFiscal() {
        Exotico iggy = new Exotico(3, "Iggy", "Pequeno", 2, "Iguana", "RFIDEX-1", "NF-123", dono);
        assertEquals("RFIDEX-1", iggy.getRfidex());
        assertEquals("NF-123", iggy.getNotaFiscal());
        assertEquals("Iguana", iggy.getEspecie());
    }

    @Test
    @DisplayName("Consulta: liga o animal e o veterinário atendidos")
    void consultaLigaAnimalEVeterinario() {
        Pet pet = new Pet(1, "Rex", "Médio", 3, "Cão", "RFID-1", dono);
        Veterinario vet = new Veterinario("CRMV-1", "Dra. Bia", 40, LocalDate.of(2010, 1, 1));
        Consulta consulta = new Consulta(5, "Otite", pet, vet);
        assertEquals(5, consulta.getid());
        assertEquals("Otite", consulta.getdiagnostico());
        assertSame(pet, consulta.getanimal());
        assertSame(vet, consulta.getveterinario());
    }

    @Test
    @DisplayName("Tratamento: guarda a consulta de origem e se usa antibiótico")
    void tratamentoGuardaConsultaEAntibiotico() {
        Consulta consulta = new Consulta();
        consulta.setid(9);
        Tratamento tratamento = new Tratamento(1, "Amoxicilina 7 dias", true, consulta);
        assertTrue(tratamento.isAntibiotico());
        assertEquals(9, tratamento.getConsulta().getid());
        tratamento.setAntibiotico(false);
        assertFalse(tratamento.isAntibiotico());
    }

    @Test
    @DisplayName("Certificação: o construtor não define especialidade; ela entra pelo setter")
    void certificacaoRecebeEspecialidadePeloSetter() {
        Veterinario vet = new Veterinario("CRMV-1", "Dra. Bia", 40, LocalDate.of(2010, 1, 1));
        Certificacao cert = new Certificacao("REG-1", LocalDate.of(2015, 3, 1), "CFMV", vet);
        assertNull(cert.getEspecialidade());
        Especialidade derma = new Especialidade(2, "Dermatologia");
        cert.setEspecialidade(derma);
        assertEquals("Dermatologia", cert.getEspecialidade().getNome());
        assertEquals("CRMV-1", cert.getVeterinario().getCrmv());
    }

    @Test
    @DisplayName("Veterinário: o construtor guarda CRMV, nome, idade e data de graduação")
    void veterinarioGuardaDados() {
        Veterinario vet = new Veterinario("CRMV-1", "Dra. Bia", 40, LocalDate.of(2010, 1, 1));
        assertEquals("CRMV-1", vet.getCrmv());
        assertEquals("Dra. Bia", vet.getNome());
        assertEquals(40, vet.getIdade());
        assertEquals(LocalDate.of(2010, 1, 1), vet.getDataGraduacao());
    }
}
