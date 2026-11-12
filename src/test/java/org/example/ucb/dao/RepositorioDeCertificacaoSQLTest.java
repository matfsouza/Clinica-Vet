package org.example.ucb.dao;

import org.example.ucb.model.Certificacao;
import org.example.ucb.model.Especialidade;
import org.example.ucb.model.Veterinario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioDeCertificacaoSQLTest {

    private RepositorioDeCertificacaoSQL repositorio;
    private Veterinario vet;
    private Especialidade derma;

    @BeforeEach
    void prepararBanco() {
        BancoDeTesteH2 banco = new BancoDeTesteH2();
        repositorio = new RepositorioDeCertificacaoSQL(banco);
        vet = DadosDeTeste.veterinario("CRMV-1", "Dra. Bia");
        new RepositorioDeVeterinarioSQL(banco).salvar(vet);
        new RepositorioDeEspecialidadeSQL(banco).salvar(DadosDeTeste.especialidade("Dermatologia"));
        derma = new Especialidade(1, "Dermatologia");
    }

    private Certificacao certificacao(String registro) {
        Certificacao cert = new Certificacao(registro, LocalDate.of(2015, 3, 1), "CFMV", vet);
        cert.setEspecialidade(derma);
        return cert;
    }

    @Test
    @DisplayName("salva uma certificação e encontra pelo número de registro, com veterinário e especialidade")
    void salvaEBuscaPorRegistro() {
        repositorio.salvar(certificacao("REG-1"));

        Certificacao encontrada = repositorio.BuscarNumeroRegistro("REG-1");

        assertEquals("REG-1", encontrada.getNumeroRegistro());
        assertEquals("Dra. Bia", encontrada.getVeterinario().getNome());
        assertEquals("Dermatologia", encontrada.getEspecialidade().getNome());
    }

    @Test
    @DisplayName("lista as certificações de um veterinário com data e instituição")
    void buscaPorVeterinario() {
        repositorio.salvar(certificacao("REG-1"));
        repositorio.salvar(certificacao("REG-2"));

        List<Certificacao> doVet = repositorio.BuscarPorVet("CRMV-1");

        assertEquals(2, doVet.size());
        assertEquals(LocalDate.of(2015, 3, 1), doVet.get(0).getDataObtencao());
        assertEquals("CFMV", doVet.get(0).getInstituicaoCertificadora());
        assertEquals("Dra. Bia", doVet.get(0).getVeterinario().getNome());
    }

    @Test
    @DisplayName("listagem mostra o nome do veterinário e o da especialidade, cada um no seu campo")
    void listaComNomesCorretos() {
        repositorio.salvar(certificacao("REG-1"));

        Certificacao listada = repositorio.ListarCertificacao().get(0);

        assertEquals("Dra. Bia", listada.getVeterinario().getNome());
        assertEquals("Dermatologia", listada.getEspecialidade().getNome());
    }

    @Test
    @DisplayName("deleta uma certificação e devolve false na segunda vez")
    void deleta() {
        repositorio.salvar(certificacao("REG-1"));

        assertTrue(repositorio.deletarCertificacao("REG-1"));
        assertNull(repositorio.BuscarNumeroRegistro("REG-1"));
        assertFalse(repositorio.deletarCertificacao("REG-1"));
    }
}
