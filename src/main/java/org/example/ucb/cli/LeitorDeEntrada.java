package org.example.ucb.cli;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Lê números digitados no console sem derrubar o sistema.
 * Antes, um valor não numérico num menu lançava InputMismatchException e encerrava a CLI.
 */
public class LeitorDeEntrada {

    static final String MENSAGEM_INVALIDO = "Valor inválido. Digite um número inteiro: ";

    private final Scanner scanner;
    private final PrintStream saida;

    public LeitorDeEntrada(Scanner scanner, PrintStream saida) {
        this.scanner = scanner;
        this.saida = saida;
    }

    /** Lê uma linha inteira e repete a pergunta até receber um número inteiro. */
    public int lerInteiro() {
        while (true) {
            String linha = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                saida.print(MENSAGEM_INVALIDO);
            }
        }
    }

    /**
     * Igual a {@link #lerInteiro()}, mas aceita linha vazia e devolve null.
     * Serve para os campos de atualização em que Enter mantém o valor atual.
     */
    public Integer lerInteiroOpcional() {
        while (true) {
            String linha = scanner.nextLine().trim();
            if (linha.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                saida.print(MENSAGEM_INVALIDO);
            }
        }
    }
}
