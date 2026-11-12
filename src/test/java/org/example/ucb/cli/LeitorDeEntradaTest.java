package org.example.ucb.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeitorDeEntradaTest {

    private final ByteArrayOutputStream saida = new ByteArrayOutputStream();

    private LeitorDeEntrada leitor(String digitado) {
        return new LeitorDeEntrada(new Scanner(digitado),
                new PrintStream(saida, true, StandardCharsets.UTF_8));
    }

    private String mensagens() {
        return saida.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("devolve o número digitado, ignorando espaços")
    void leNumeroValido() {
        assertEquals(7, leitor("  7  \n").lerInteiro());
        assertEquals("", mensagens());
    }

    @Test
    @DisplayName("com uma letra no menu, avisa e pergunta de novo em vez de encerrar o sistema")
    void repetePerguntaComLetra() {
        int opcao = leitor("abc\n3\n").lerInteiro();

        assertEquals(3, opcao);
        assertEquals(LeitorDeEntrada.MENSAGEM_INVALIDO, mensagens());
    }

    @Test
    @DisplayName("linhas vazias e números quebrados também são recusados")
    void recusaVazioEDecimal() {
        int opcao = leitor("\n2,5\n-1\n").lerInteiro();

        assertEquals(-1, opcao);
        assertEquals(LeitorDeEntrada.MENSAGEM_INVALIDO.repeat(2), mensagens());
    }

    @Test
    @DisplayName("leitura opcional: Enter devolve null e uma letra pede o valor de novo")
    void leituraOpcional() {
        LeitorDeEntrada leitor = leitor("\nx\n4\n");

        assertNull(leitor.lerInteiroOpcional());
        assertEquals(4, leitor.lerInteiroOpcional());
        assertEquals(LeitorDeEntrada.MENSAGEM_INVALIDO, mensagens());
    }

    @Test
    @DisplayName("lê uma linha por vez, sem deixar sobra para a próxima leitura de texto")
    void naoDeixaSobraNoBuffer() {
        Scanner scanner = new Scanner("5\nRex\n");
        LeitorDeEntrada leitor = new LeitorDeEntrada(scanner, new PrintStream(saida));

        assertEquals(5, leitor.lerInteiro());
        assertEquals("Rex", scanner.nextLine());
    }
}
