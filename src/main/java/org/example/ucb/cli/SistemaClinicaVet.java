package org.example.ucb.cli;

import org.example.ucb.control.*;
import org.example.ucb.dao.*;
import org.example.ucb.model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class SistemaClinicaVet {

    private static final Scanner entrada = new Scanner(System.in);
    private static final LeitorDeEntrada leitor = new LeitorDeEntrada(entrada, System.out);
    private static RepositorioDeTratamento repositorioDeTratamento;
    private static RepositorioDeEspecialidade repositorioDeEspecialidade;
    private static RepositorioDeAnimal repositorioDeAnimal;
    private static RepositorioDeDono repositorioDeDono;
    private static RepositorioDeVeterinario repositorioDeVeterinario;
    private static RepositorioDeCertificacao repositorioDeCertificacao;
    private static RepositorioDeConsulta repositorioDeConsulta;

    public static void main(String[] args) {
        try {
            configurarDependencias();
            exibirMenuPrincipal();
        } catch (Exception e) {
            System.err.println("ERRO FATAL NO SISTEMA: " + e.getMessage());
        } finally {
            entrada.close();
        }
    }

    private static void configurarDependencias() {
        // Único ponto que escolhe o banco: os DAOs recebem a conexão pronta.
        ConexaoBD conexao = new ConexaoMySQL();
        repositorioDeTratamento = new RepositorioDeTratamentoSQL(conexao);
        repositorioDeEspecialidade = new RepositorioDeEspecialidadeSQL(conexao);
        repositorioDeAnimal = new RepositorioDeAnimalSQL(conexao);
        repositorioDeDono = new RepositorioDeDonoSQL(conexao);
        repositorioDeVeterinario = new RepositorioDeVeterinarioSQL(conexao);
        repositorioDeCertificacao = new RepositorioDeCertificacaoSQL(conexao);
        repositorioDeConsulta = new RepositorioDeConsultaSQL(conexao);
    }

    private static void exibirMenuPrincipal() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n======= CLÍNICA VETERINÁRIA - MENU PRINCIPAL =======");
            System.out.println("1. Gerenciar Donos");
            System.out.println("2. Gerenciar Veterinários");
            System.out.println("3. Gerenciar Animais");
            System.out.println("4. Gerenciar Tratamentos");
            System.out.println("5. Gerenciar Especialidades");
            System.out.println("6. Gerenciar Certificações");
            System.out.println("7. Gerenciar Consultas");
            System.out.println("0. Sair do Sistema");
            System.out.print("Escolha uma área para gerenciar: ");

            int opcao = leitor.lerInteiro();

            switch (opcao) {
                case 1:
                    exibirMenuDono();
                    break;
                case 2:
                    exibirMenuVeterinario();
                    break;
                case 3:
                    exibirMenuAnimais();
                    break;
                case 4:
                    exibirMenuTratamentos();
                    break;
                case 5:
                    exibirMenuEspecialidades();
                    break;
                case 6:
                    exibirMenuCertificacoes();
                    break;
                case 7:
                    exibirMenuConsultas();
                    break;
                case 0:
                    sair = true;
                    System.out.println("Obrigado por usar o sistema!");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void exibirMenuConsultas() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n--- Menu de Gerenciamento de Consultas ---");
            System.out.println("1. Agendar (Cadastrar) Nova Consulta");
            System.out.println("2. Listar Todas as Consultas");
            System.out.println("3. Buscar Consulta por ID");
            System.out.println("4. Listar Consultas de um Animal (por ID)");
            System.out.println("5. Atualizar Diagnóstico da Consulta");
            System.out.println("6. Cancelar (Deletar) Consulta");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opcao = leitor.lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.println("\n--- Agendar Nova Consulta ---");

                    System.out.print("Digite o ID do Animal para a consulta.html: ");
                    int idAnimalConsulta = leitor.lerInteiro();

                    Animal animalConsulta = repositorioDeAnimal.BuscarPorId(idAnimalConsulta);

                    if (animalConsulta == null) {
                        System.out.println("Erro: Animal com ID " + idAnimalConsulta + " não encontrado.");
                        break;
                    }
                    System.out.println("Animal encontrado: " + animalConsulta.getNome());

                    System.out.print("Digite o CRMV do Veterinário: ");
                    String crmvConsulta = entrada.nextLine();
                    Veterinario vetConsulta = repositorioDeVeterinario.BuscarVet(crmvConsulta);

                    if (vetConsulta == null) {
                        System.out.println("Erro: Veterinário com CRMV " + crmvConsulta + " não encontrado.");
                        break;
                    }
                    System.out.println("Veterinário encontrado: " + vetConsulta.getNome());

                    System.out.print("Digite o diagnóstico inicial (ou deixe em branco): ");
                    String diagnostico = entrada.nextLine();
                    Consulta novaConsulta = new Consulta(0, diagnostico, animalConsulta, vetConsulta);
                    repositorioDeConsulta.salvar(novaConsulta);
                    break;

                case 2:
                    System.out.println("\n--- Lista de Todas as Consultas ---");
                    List<Consulta> todasConsultas = repositorioDeConsulta.ListarConsulta();

                    if (todasConsultas == null || todasConsultas.isEmpty()) {
                        System.out.println("Nenhuma consulta.html agendada.");
                    } else {
                        for (Consulta c : todasConsultas) {
                            System.out.println("--------------------");
                            System.out.println("ID Consulta: " + c.getid());
                            System.out.println("Animal: " + (c.getanimal() != null ? c.getanimal().getNome() : "N/A"));
                            System.out.println("Veterinário: " + (c.getveterinario() != null ? c.getveterinario().getNome() : "N/A"));
                            System.out.println("Diagnóstico: " + c.getdiagnostico());
                        }
                        System.out.println("--------------------");
                    }
                    break;

                case 3:
                    System.out.println("\n--- Buscar Consulta por ID ---");
                    System.out.print("Digite o ID da consulta.html: ");
                    int idBuscaConsulta = leitor.lerInteiro();
                    Consulta consultaEncontrada = repositorioDeConsulta.BuscarConsulta(idBuscaConsulta);

                    if (consultaEncontrada != null) {
                        System.out.println("\n--- Consulta Encontrada ---");
                        System.out.println("ID Consulta: " + consultaEncontrada.getid());
                        System.out.println("Animal: " + (consultaEncontrada.getanimal() != null ? consultaEncontrada.getanimal().getNome() : "N/A"));
                        System.out.println("Veterinário: " + (consultaEncontrada.getveterinario() != null ? consultaEncontrada.getveterinario().getNome() : "N/A"));
                        System.out.println("Diagnóstico: " + consultaEncontrada.getdiagnostico());
                        System.out.println("-------------------------");
                    } else {
                        System.out.println("\nConsulta com ID " + idBuscaConsulta + " não encontrada.");
                    }
                    break;

                case 4:
                    System.out.println("\n--- Listar Consultas por Animal ---");
                    System.out.print("Digite o ID do Animal: ");
                    int idAnimalBusca = leitor.lerInteiro();

                    List<Consulta> consultasDoAnimal = repositorioDeConsulta.BuscarPorAnimal(idAnimalBusca);

                    if (consultasDoAnimal == null || consultasDoAnimal.isEmpty()) {
                        System.out.println("Nenhuma consulta.html encontrada para o animal com ID " + idAnimalBusca);
                    } else {
                        System.out.println("\n--- Consultas Encontradas para o Animal ID " + idAnimalBusca + " ---");
                        for (Consulta c : consultasDoAnimal) {
                            System.out.println("--------------------");
                            System.out.println("ID Consulta: " + c.getid());
                            System.out.println("Veterinário: " + (c.getveterinario() != null ? c.getveterinario().getNome() : "N/A"));
                            System.out.println("Diagnóstico: " + c.getdiagnostico());
                        }
                        System.out.println("--------------------");
                    }
                    break;

                case 5:
                    System.out.println("\n--- Atualizar Diagnóstico da Consulta ---");
                    System.out.print("Digite o ID da consulta.html que deseja atualizar: ");
                    int idConsultaAtt = leitor.lerInteiro();

                    Consulta consultaAtt = repositorioDeConsulta.BuscarConsulta(idConsultaAtt);

                    if (consultaAtt == null) {
                        System.out.println("Consulta não encontrada.");
                        break;
                    }

                    System.out.println("Consulta encontrada. Animal: " + consultaAtt.getanimal().getNome() + ", Vet: " + consultaAtt.getveterinario().getNome());
                    System.out.print("Digite o NOVO diagnóstico (Atual: " + consultaAtt.getdiagnostico() + "): ");
                    String novoDiagnostico = entrada.nextLine();

                    consultaAtt.setdiagnostico(novoDiagnostico);
                    repositorioDeConsulta.atualizarConsulta(consultaAtt);
                    break;

                case 6:
                    System.out.println("\n--- Cancelar (Deletar) Consulta ---");
                    System.out.print("Digite o ID da consulta.html a ser deletada: ");
                    int idConsultaDel = leitor.lerInteiro();

                    System.out.print("Tem certeza que deseja deletar esta consulta.html (ID: " + idConsultaDel + ")? (S/N): ");
                    if (entrada.nextLine().equalsIgnoreCase("S")) {
                        boolean deletado = repositorioDeConsulta.deletarConsulta(idConsultaDel); //
                    } else {
                        System.out.println("Operação cancelada.");
                    }
                    break;

                case 0:
                    sair = true;
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    //Parte do Victor Caldas - Dono
    private static void exibirMenuDono() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n--- Menu de Gerenciamento de Donos ---");
            System.out.println("1. Cadastrar Novo Dono");
            System.out.println("2. Listar Todos os Donos");
            System.out.println("3. Buscar Dono por CPF");
            System.out.println("4. Atualizar Dono");
            System.out.println("5. Deletar Dono");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opcao = leitor.lerInteiro();
            switch (opcao) {
                case 1:
                    try {
                        System.out.println("\n--- Cadastrar Novo Dono ---");
                        System.out.print("CPF: ");
                        String cpf = entrada.nextLine();
                        System.out.print("Nome: ");
                        String nome = entrada.nextLine();
                        System.out.print("Endereço: ");
                        String endereco = entrada.nextLine();
                        System.out.print("Data de Nascimento (dd/MM/yyyy): ");
                        String dataTexto = entrada.nextLine();
                        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate dataNascimento = LocalDate.parse(dataTexto, formatador);
                        Dono novoDono = new Dono(cpf, dataNascimento, endereco, nome);
                        repositorioDeDono.salvar(novoDono);
                    } catch (Exception e) {
                        System.err.println("Erro ao cadastrar dono. Verifique o formato da data.");
                    }
                    break;
                case 2:
                    List<Dono> donos = repositorioDeDono.ListarDono();
                    if (donos.isEmpty()) {
                        System.out.println("Nenhum dono cadastrado.");
                    } else {
                        System.out.println("\n--- Lista de Donos Cadastrados ---");
                        for (Dono d : donos) {
                            System.out.println("CPF: " + d.getCPF() + " | Nome: " + d.getNome());
                        }
                    }
                    break;
                case 3:
                    System.out.print("\nDigite o CPF para busca: ");
                    String cpfBusca = entrada.nextLine();
                    Dono donoEncontrado = repositorioDeDono.BuscarPorCPF(cpfBusca);
                    if (donoEncontrado != null) {
                        System.out.println("--- Dono Encontrado ---");
                        System.out.println("CPF: " + donoEncontrado.getCPF());
                        System.out.println("Nome: " + donoEncontrado.getNome());
                        System.out.println("Endereço: " + donoEncontrado.getEndereco());
                        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        System.out.println("Data de Nascimento: " + donoEncontrado.getDataNascimento().format(f));
                    } else {
                        System.out.println("Dono com CPF " + cpfBusca + " não encontrado.");
                    }
                    break;
                case 4:
                    System.out.print("\nDigite o CPF do dono que deseja atualizar: ");
                    String cpfAtt = entrada.nextLine();
                    Dono donoAtt = repositorioDeDono.BuscarPorCPF(cpfAtt);
                    if (donoAtt == null) {
                        System.out.println("Dono não encontrado.");
                        break;
                    }
                    System.out.println("Deixe o campo em branco para não alterar.");
                    System.out.print("Novo Nome (" + donoAtt.getNome() + "): ");
                    String nomeAtt = entrada.nextLine();
                    if (!nomeAtt.trim().isEmpty()) donoAtt.setNome(nomeAtt);
                    System.out.print("Novo Endereço (" + donoAtt.getEndereco() + "): ");
                    String enderecoAtt = entrada.nextLine();
                    if (!enderecoAtt.trim().isEmpty()) donoAtt.setEndereco(enderecoAtt);
                    repositorioDeDono.atualizar(donoAtt);
                    System.out.println("Dono atualizado com sucesso!");
                    break;
                case 5:
                    System.out.print("\nDigite o CPF do dono que deseja deletar: ");
                    String cpfDel = entrada.nextLine();
                    System.out.print("Tem certeza que deseja deletar? (S/N): ");
                    if (entrada.nextLine().equalsIgnoreCase("S")) {
                        if (repositorioDeDono.deletarDono(cpfDel)) {
                            System.out.println("Dono deletado com sucesso.");
                        } else {
                            System.err.println("Erro: Dono não encontrado.");
                        }
                    } else {
                        System.out.println("Operação cancelada.");
                    }
                    break;
                case 0:
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    // Parte do Victor Caldas - Veterinário
    private static void exibirMenuVeterinario() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n--- Menu de Gerenciamento de Veterinários ---");
            System.out.println("1. Cadastrar Novo Veterinário");
            System.out.println("2. Listar Todos os Veterinários");
            System.out.println("3. Buscar Veterinário por CRMV");
            System.out.println("4. Atualizar Veterinário");
            System.out.println("5. Deletar Veterinário");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opcao = leitor.lerInteiro();

            switch (opcao) {
                case 1:
                    try {
                        System.out.println("\n--- Cadastrar Novo Veterinário ---");
                        System.out.print("CRMV: ");
                        String crmv = entrada.nextLine();
                        System.out.print("Nome: ");
                        String nome = entrada.nextLine();
                        System.out.print("Idade: ");
                        int idade = leitor.lerInteiro();
                        System.out.print("Data de Graduação (dd/MM/yyyy): ");
                        String dataTexto = entrada.nextLine();
                        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate dataGraduacao = LocalDate.parse(dataTexto, formatador);
                        Veterinario novoVet = new Veterinario(crmv, nome, idade, dataGraduacao);
                        repositorioDeVeterinario.salvar(novoVet);
                    } catch (Exception e) {
                        System.err.println("Erro ao cadastrar veterinário. Verifique os dados e o formato da data.");
                    }
                    break;
                case 2:
                    List<Veterinario> veterinarios = repositorioDeVeterinario.ListarVet();
                    if (veterinarios.isEmpty()) {
                        System.out.println("Nenhum veterinário cadastrado.");
                    } else {
                        System.out.println("\n--- Lista de Veterinários Cadastrados ---");
                        for (Veterinario vet : veterinarios) {
                            System.out.println("CRMV: " + vet.getCrmv() + " | Nome: " + vet.getNome());
                        }
                    }
                    break;
                case 3:
                    System.out.print("\nDigite o CRMV para busca: ");
                    String crmvBusca = entrada.nextLine();
                    Veterinario vetEncontrado = repositorioDeVeterinario.BuscarVet(crmvBusca);
                    if (vetEncontrado != null) {
                        System.out.println("--- Veterinário Encontrado ---");
                        System.out.println("CRMV: " + vetEncontrado.getCrmv());
                        System.out.println("Nome: " + vetEncontrado.getNome());
                        System.out.println("Idade: " + vetEncontrado.getIdade());
                        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        System.out.println("Data de Graduação: " + vetEncontrado.getDataGraduacao().format(f));
                    } else {
                        System.out.println("Veterinário com CRMV " + crmvBusca + " não encontrado.");
                    }
                    break;
                case 4:
                    System.out.print("\nDigite o CRMV do veterinário que deseja atualizar: ");
                    String crmvAtt = entrada.nextLine();
                    Veterinario vetAtt = repositorioDeVeterinario.BuscarVet(crmvAtt);
                    if (vetAtt == null) {
                        System.out.println("Veterinário não encontrado.");
                        break;
                    }
                    System.out.println("Deixe o campo em branco para não alterar.");
                    System.out.print("Novo Nome (" + vetAtt.getNome() + "): ");
                    String nomeAtt = entrada.nextLine();
                    if (!nomeAtt.trim().isEmpty()) vetAtt.setNome(nomeAtt);
                    repositorioDeVeterinario.atualizar(vetAtt);
                    System.out.println("Veterinário atualizado com sucesso!");
                    break;
                case 5:
                    System.out.print("\nDigite o CRMV do veterinário a ser deletado: ");
                    String crmvDel = entrada.nextLine();
                    System.out.print("Tem certeza que deseja deletar? (S/N): ");
                    if (entrada.nextLine().equalsIgnoreCase("S")) {
                        if (repositorioDeVeterinario.deletarVet(crmvDel)) {
                            System.out.println("Veterinário deletado com sucesso.");
                        } else {
                            System.err.println("Erro: Veterinário não encontrado.");
                        }
                    } else {
                        System.out.println("Operação cancelada.");
                    }
                    break;
                case 0:
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    // PARTE DO VÍTOR - TRATAMENTOS;
    private static void exibirMenuTratamentos() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n--- Menu de Gerenciamento de Tratamentos ---");
            System.out.println("1. Adicionar novo tratamento a uma consulta.html");
            System.out.println("2. Buscar tratamento por ID");
            System.out.println("3. Listar tratamentos de uma consulta.html");
            System.out.println("0. Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            int opcao = leitor.lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.println("\n--- Adicionar Novo Tratamento ---");

                    System.out.print("Digite o ID da consulta.html à qual este tratamento pertence: ");
                    int idConsulta = leitor.lerInteiro();

                    System.out.print("Digite a descrição do tratamento: ");
                    String descricao = entrada.nextLine();

                    System.out.print("O tratamento utiliza antibiótico? (s/n): ");
                    String usaAntibioticoInput = entrada.nextLine();
                    boolean usaAntibiotico = usaAntibioticoInput.equalsIgnoreCase("s");

                    Consulta consulta = new Consulta(idConsulta, null, null, null);


                    Tratamento novoTratamento = new Tratamento(0, descricao, usaAntibiotico, consulta);

                    repositorioDeTratamento.salvar(novoTratamento);

                    break;
                case 2:
                    System.out.println("\n--- Buscar Tratamento por ID ---");
                    System.out.print("Digite o ID do tratamento que deseja buscar: ");
                    int idParaBuscar = leitor.lerInteiro();
                    Tratamento tratamentoEncontrado = repositorioDeTratamento.BuscarTratamento(idParaBuscar);

                    if (tratamentoEncontrado != null) {

                        System.out.println("\n--- Tratamento Encontrado ---");
                        System.out.println("ID do Tratamento: " + tratamentoEncontrado.getId());
                        System.out.println("ID da Consulta Associada: " + tratamentoEncontrado.getConsulta().getid());
                        System.out.println("Descrição: " + tratamentoEncontrado.getDescricao());
                        System.out.println("Usa Antibiótico: " + (tratamentoEncontrado.isAntibiotico() ? "Sim" : "Não"));
                        System.out.println("-----------------------------");
                    } else {

                        System.out.println("\nTratamento com o ID " + idParaBuscar + " não encontrado.");
                    }
                    break;
                case 3:
                    System.out.println("\n--- Listar Tratamentos por Consulta ---");
                    System.out.print("Digite o ID da consulta.html para ver os tratamentos associados: ");
                    int idConsultaParaListar = leitor.lerInteiro();

                    List<Tratamento> tratamentosDaConsulta = repositorioDeTratamento.BuscarPorConsulta(idConsultaParaListar);

                    if (tratamentosDaConsulta != null && !tratamentosDaConsulta.isEmpty()) {
                        System.out.println("\n--- Tratamentos Encontrados para a Consulta ID: " + idConsultaParaListar + " ---");

                        for (Tratamento tratamento : tratamentosDaConsulta) {
                            System.out.println("-----------------------------");
                            System.out.println("ID do Tratamento: " + tratamento.getId());
                            System.out.println("Descrição: " + tratamento.getDescricao());
                            System.out.println("Usa Antibiótico: " + (tratamento.isAntibiotico() ? "Sim" : "Não"));
                        }
                        System.out.println("-----------------------------");
                    } else {

                        System.out.println("\nNenhum tratamento encontrado para a consulta.html com o ID " + idConsultaParaListar + ".");
                    }

                    break;
                case 0:
                    sair = true;
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    //PARTE DO RENAN - ANIMAL;
    private static void exibirMenuAnimais() {
        boolean sair = false;

        while (!sair) {
            System.out.println("\n--- Menu de Animais ---");
            System.out.println("1. Cadastrar Animal");
            System.out.println("2. Listar Todos os Animais");
            System.out.println("3. Buscar Animal por ID");
            System.out.println("4. Atualizar Animal");
            System.out.println("5. Excluir Animal");
            System.out.println("6. Buscar Animais por Dono (CPF)");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opcao = leitor.lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.println("--- Cadastrar Novo Animal ---");
                    System.out.print("Digite o CPF do Dono: ");
                    String cpfDono = entrada.nextLine();
                    Dono dono = repositorioDeDono.BuscarPorCPF(cpfDono);

                    if (dono == null) {
                        System.out.println("Erro: Dono com CPF " + cpfDono + " não encontrado.");
                        System.out.println("Por favor, cadastre o dono antes de cadastrar o animal.");
                        break;
                    }
                    System.out.println("Dono encontrado: " + dono.getNome());

                    System.out.println("Qual o tipo de animal?");
                    System.out.println("1. Pet (Doméstico)");
                    System.out.println("2. Animal Exótico");
                    System.out.print("Opção: ");
                    int tipo = leitor.lerInteiro();
                    System.out.print("Nome: ");
                    String nome = entrada.nextLine();
                    System.out.print("Espécie: ");
                    String especie = entrada.nextLine();
                    System.out.print("Porte (P, M, G): ");
                    String porte = entrada.nextLine();
                    System.out.print("Idade: ");
                    int idade = leitor.lerInteiro();
                    Animal novoAnimal;

                    if (tipo == 1) { //Só pra indentificar se é pet ou exótico (Pet = 1, Exótico = 2)
                        System.out.print("RFID (Chip de identificação): ");
                        String rfid = entrada.nextLine();
                        Pet pet = new Pet();
                        pet.setrfid(rfid);
                        novoAnimal = pet;

                    } else if (tipo == 2) {
                        System.out.print("RFIDEX (Identificação de exótico): ");
                        String rfidex = entrada.nextLine();
                        System.out.print("Nota Fiscal: ");
                        String notaFiscal = entrada.nextLine();
                        Exotico exotico = new Exotico();
                        exotico.setRfidex(rfidex);
                        exotico.setNotaFiscal(notaFiscal);
                        novoAnimal = exotico;

                    } else {
                        System.out.println("Tipo inválido. Cadastro cancelado.");
                        break;
                    }
                    novoAnimal.setNome(nome);
                    novoAnimal.setEspecie(especie);
                    novoAnimal.setPorte(porte);
                    novoAnimal.setIdade(idade);
                    novoAnimal.setDono(dono);
                    repositorioDeAnimal.salvar(novoAnimal);
                    break;

                case 2:
                    System.out.println("--- Lista de Animais Cadastrados ---");
                    List<Animal> animais = repositorioDeAnimal.ListarTodos();

                    if (animais.isEmpty()) {
                        System.out.println("Nenhum animal cadastrado.");
                    } else {

                        for (Animal animal : animais) {
                            System.out.println("--------------------");
                            System.out.println("ID: " + animal.getId());
                            System.out.println("Nome: " + animal.getNome());
                            System.out.println("Espécie: " + animal.getEspecie());
                            System.out.println("Idade: " + animal.getIdade());


                            System.out.println("Dono: " + animal.getDono().getNome());
                            if (animal instanceof Pet) {
                                System.out.println("Tipo: Pet");
                                System.out.println("RFID: " + ((Pet) animal).getrfid());
                            } else if (animal instanceof Exotico) {
                                System.out.println("Tipo: Exótico");
                                System.out.println("RFIDEX: " + ((Exotico) animal).getRfidex());
                                System.out.println("Nota Fiscal: " + ((Exotico) animal).getNotaFiscal());
                            }
                        }
                        System.out.println("--------------------");
                    }
                    break;

                case 3:
                    System.out.println("--- Buscar Animal por ID ---");
                    System.out.print("Digite o ID do animal: ");
                    int idBusca = leitor.lerInteiro();

                    Animal animalEncontrado = repositorioDeAnimal.BuscarPorId(idBusca);

                    if (animalEncontrado != null) {
                        System.out.println("Animal encontrado:");
                        System.out.println("ID: " + animalEncontrado.getId());
                        System.out.println("Nome: " + animalEncontrado.getNome());
                        System.out.println("Espécie: " + animalEncontrado.getEspecie());
                        System.out.println("Idade: " + animalEncontrado.getIdade());

                        if (animalEncontrado instanceof Pet) {
                            System.out.println("Tipo: Pet");
                            System.out.println("RFID: " + ((Pet) animalEncontrado).getrfid());
                        } else if (animalEncontrado instanceof Exotico) {
                            System.out.println("Tipo: Exótico");
                            System.out.println("RFIDEX: " + ((Exotico) animalEncontrado).getRfidex());
                            System.out.println("Nota Fiscal: " + ((Exotico) animalEncontrado).getNotaFiscal());
                        }

                    } else {
                        System.out.println("Animal com o ID " + idBusca + " não encontrado.");
                    }
                    break;

                case 4:
                    System.out.println("--- Atualizar Animal ---");
                    System.out.print("Digite o ID do animal que deseja atualizar: ");
                    int idAtt = leitor.lerInteiro();

                    Animal animalAtt = repositorioDeAnimal.BuscarPorId(idAtt);

                    if (animalAtt == null) {
                        System.out.println("Animal não encontrado.");
                        break;
                    }
                    System.out.println("Animal encontrado: " + animalAtt.getNome());
                    System.out.println("Deixe o campo em branco para não alterar.");

                    System.out.print("Novo Nome (" + animalAtt.getNome() + "): ");
                    String nomeAtt = entrada.nextLine();
                    if (!nomeAtt.trim().isEmpty()) {
                        animalAtt.setNome(nomeAtt);
                    }
                    System.out.print("Nova Espécie (" + animalAtt.getEspecie() + "): ");
                    String especieAtt = entrada.nextLine();
                    if (!especieAtt.trim().isEmpty()) {
                        animalAtt.setEspecie(especieAtt);
                    }

                    System.out.print("Novo Porte (" + animalAtt.getPorte() + "): ");
                    String porteAtt = entrada.nextLine();
                    if (!porteAtt.trim().isEmpty()) {
                        animalAtt.setPorte(porteAtt);
                    }

                    System.out.print("Nova Idade (" + animalAtt.getIdade() + "): ");
                    Integer idadeAtt = leitor.lerInteiroOpcional();
                    if (idadeAtt != null) {
                        animalAtt.setIdade(idadeAtt);
                    }
                    if (animalAtt instanceof Pet) {
                        System.out.print("Novo RFID (" + ((Pet) animalAtt).getrfid() + "): ");
                        String rfidAtt = entrada.nextLine();
                        if (!rfidAtt.trim().isEmpty()) {
                            ((Pet) animalAtt).setrfid(rfidAtt);
                        }
                    } else if (animalAtt instanceof Exotico) {
                        System.out.print("Novo RFIDEX (" + ((Exotico) animalAtt).getRfidex() + "): ");
                        String rfidexAtt = entrada.nextLine();
                        if (!rfidexAtt.trim().isEmpty()) {
                            ((Exotico) animalAtt).setRfidex(rfidexAtt);
                        }

                        System.out.print("Nova Nota Fiscal (" + ((Exotico) animalAtt).getNotaFiscal() + "): ");
                        String notaAtt = entrada.nextLine();
                        if (!notaAtt.trim().isEmpty()) {
                            ((Exotico) animalAtt).setNotaFiscal(notaAtt);
                        }
                    }

                    repositorioDeAnimal.atualizar(animalAtt);
                    System.out.println("Animal atualizado com sucesso!");

                    break;

                case 5:
                    System.out.println("--- Excluir Animal ---");
                    System.out.print("Digite o ID do animal que deseja excluir: ");
                    int idDel = leitor.lerInteiro();

                    Animal animalDel = repositorioDeAnimal.BuscarPorId(idDel); //
                    if (animalDel == null) {
                        System.out.println("Animal não encontrado.");
                        break;
                    }

                    System.out.println("Tem certeza que deseja excluir o animal: " + animalDel.getNome() + "? (S/N)"); //
                    String confirmacao = entrada.nextLine();

                    if (confirmacao.equalsIgnoreCase("S")) {
                        boolean deletado = repositorioDeAnimal.deletarAnimal(idDel);
                        if (deletado) {
                            System.out.println("Animal excluído com sucesso.");
                        } else {
                            System.out.println("Erro ao excluir animal.");
                        }
                    } else {
                        System.out.println("Exclusão cancelada.");
                    }
                    break;

                case 6:
                    System.out.println("--- Buscar Animais por Dono ---");
                    System.out.print("Digite o CPF do Dono: ");
                    String cpfDonoBusca = entrada.nextLine();

                    List<Animal> animaisDoDono = repositorioDeAnimal.BuscarPorDono(cpfDonoBusca); //

                    if (animaisDoDono.isEmpty()) {
                        System.out.println("Nenhum animal encontrado para este dono.");
                    } else {
                        System.out.println("Animais encontrados para o CPF: " + cpfDonoBusca);
                        for (Animal animalDono : animaisDoDono) {
                            System.out.println("--------------------");
                            System.out.println("ID: " + animalDono.getId());
                            System.out.println("Nome: " + animalDono.getNome());
                            System.out.println("Espécie: " + animalDono.getEspecie());


                            if (animalDono instanceof Pet) {
                                System.out.println("Tipo: Pet");
                                System.out.println("RFID: " + ((Pet) animalDono).getrfid());
                            } else if (animalDono instanceof Exotico) {
                                System.out.println("Tipo: Exótico");
                                System.out.println("RFIDEX: " + ((Exotico) animalDono).getRfidex());
                                System.out.println("Nota Fiscal: " + ((Exotico) animalDono).getNotaFiscal());
                            }
                        }
                        System.out.println("--------------------");
                    }
                    break;

                case 0:
                    sair = true;
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    // PARTE DO VÍTOR - ESPECIALIDADES;
    private static void exibirMenuEspecialidades() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n--- Menu de Gerenciamento de Especialidades ---");
            System.out.println("1. Cadastrar nova especialidade");
            System.out.println("2. Buscar especialidade por ID");
            System.out.println("3. Listar todas as especialidades");
            System.out.println("4. Listar especialidades de um veterinário (por CRMV)");
            System.out.println("5. Atualizar nome da especialidade");
            System.out.println("6. Deletar especialidade");
            System.out.println("0. Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            int opcao = leitor.lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.println("\n--- Cadastrar Nova Especialidade ---");
                    System.out.print("Digite o nome da nova especialidade: ");
                    String nomeEspecialidade = entrada.nextLine();

                    Especialidade novaEspecialidade = new Especialidade(0, nomeEspecialidade);
                    repositorioDeEspecialidade.salvar(novaEspecialidade);
                    break;

                case 2:
                    System.out.println("\n--- Buscar Especialidade por ID ---");
                    System.out.print("Digite o ID da especialidade que deseja buscar: ");
                    int idParaBuscar = leitor.lerInteiro();

                    Especialidade especialidadeEncontrada = repositorioDeEspecialidade.BuscarEspecialidade(idParaBuscar);

                    if (especialidadeEncontrada != null) {
                        System.out.println("\n--- Especialidade Encontrada ---");
                        System.out.println("ID: " + especialidadeEncontrada.getId());
                        System.out.println("Nome: " + especialidadeEncontrada.getNome());
                        System.out.println("--------------------------------");
                    } else {
                        System.out.println("\nEspecialidade com o ID " + idParaBuscar + " não encontrada.");
                    }
                    break;

                case 3:
                    System.out.println("\n--- Lista de Todas as Especialidades ---");
                    List<Especialidade> todasAsEspecialidades = repositorioDeEspecialidade.ListarEspecialidade();

                    if (todasAsEspecialidades != null && !todasAsEspecialidades.isEmpty()) {
                        for (Especialidade especialidade : todasAsEspecialidades) {
                            System.out.println("ID: " + especialidade.getId() + " | Nome: " + especialidade.getNome());
                        }
                        System.out.println("--------------------------------------");
                    } else {
                        System.out.println("\nNenhuma especialidade cadastrada no sistema.");
                    }
                    break;

                case 4:
                    System.out.println("\n--- Listar Especialidades por Veterinário ---");
                    System.out.print("Digite o CRMV do veterinário: ");
                    String crmvParaBuscar = entrada.nextLine();
                    List<Especialidade> especialidadesDoVet = repositorioDeEspecialidade.BuscarEspPorVet(crmvParaBuscar);

                    if (especialidadesDoVet != null && !especialidadesDoVet.isEmpty()) {
                        System.out.println("\n--- Especialidades Encontradas para o CRMV: " + crmvParaBuscar + " ---");
                        for (Especialidade especialidade : especialidadesDoVet) {
                            System.out.println("ID: " + especialidade.getId() + " | Nome: " + especialidade.getNome());
                        }
                        System.out.println("----------------------------------------------------");
                    } else {
                        System.out.println("\nNenhuma especialidade encontrada para o veterinário com o CRMV " + crmvParaBuscar + ".");
                    }
                    break;

                case 5:
                    System.out.println("\n--- Atualizar Nome da Especialidade ---");
                    System.out.print("Digite o ID da especialidade que deseja atualizar: ");
                    int idAtt = leitor.lerInteiro();

                    Especialidade espAtt = repositorioDeEspecialidade.BuscarEspecialidade(idAtt);
                    if (espAtt == null) {
                        System.out.println("Especialidade não encontrada.");
                        break;
                    }

                    System.out.print("Digite o NOVO nome para a especialidade (Atual: " + espAtt.getNome() + "): ");
                    String novoNome = entrada.nextLine();
                    espAtt.setNome(novoNome);

                    repositorioDeEspecialidade.atualizarEspecialidade(espAtt);
                    // Mensagem de sucesso vem do DAO
                    break;

                case 6:
                    System.out.println("\n--- Deletar Especialidade ---");
                    System.out.print("Digite o ID da especialidade que deseja deletar: ");
                    int idDel = leitor.lerInteiro();

                    System.out.print("Tem certeza que deseja deletar? (S/N): ");
                    if (entrada.nextLine().equalsIgnoreCase("S")) {
                        boolean deletado = repositorioDeEspecialidade.deletarEspecialidade(idDel);
                        if (deletado) {
                            System.out.println("Especialidade deletada com sucesso.");
                        } else {
                            System.err.println("Erro: Especialidade não encontrada ou não pôde ser deletada (verifique se está sendo usada em alguma Certificação).");
                        }
                    } else {
                        System.out.println("Operação cancelada.");
                    }
                    break;

                case 0:
                    sair = true;
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    private static void exibirMenuCertificacoes() {
        boolean sair = false;

        while (!sair) {
            System.out.println("\n--- Menu de Certificações ---");
            System.out.println("1. Cadastrar Certificação (Ligar Vet a Especialidade)");
            System.out.println("2. Listar Todas as Certificações");
            System.out.println("3. Buscar Certificação por Número de Registro");
            System.out.println("4. Listar Certificações por Veterinário (CRMV)");
            System.out.println("5. Excluir Certificação");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opcao = leitor.lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.println("--- Cadastrar (Associar) Certificação ---");

                    System.out.print("Digite o CRMV do Veterinário: ");
                    String crmv = entrada.nextLine();
                    Veterinario vet = repositorioDeVeterinario.BuscarVet(crmv);

                    if (vet == null) {
                        System.out.println("Erro: Veterinário com CRMV " + crmv + " não encontrado.");
                        break;
                    }
                    System.out.println("Veterinário encontrado: " + vet.getNome());

                    System.out.print("Digite o ID da Especialidade (que é auto_increment): ");
                    int espId = leitor.lerInteiro();
                    Especialidade esp = repositorioDeEspecialidade.BuscarEspecialidade(espId);

                    if (esp == null) {
                        System.out.println("Erro: Especialidade com ID " + espId + " não encontrada.");
                        break;
                    }
                    System.out.println("Especialidade encontrada: " + esp.getNome()); //
                    System.out.print("Número do Registro (Ex: 'REG-DF-24-001A'): ");
                    String numReg = entrada.nextLine();
                    System.out.print("Instituição Certificadora: ");
                    String inst = entrada.nextLine();

                    System.out.print("Data de Obtenção (Use o formato AAAA-MM-DD): ");
                    String dataStr = entrada.nextLine();
                    LocalDate dataObtencao = LocalDate.parse(dataStr);

                    Certificacao novaCert = new Certificacao();

                    novaCert.setNumeroRegistro(numReg);
                    novaCert.setDataObtencao(dataObtencao);
                    novaCert.setInstituicaoCertificadora(inst);
                    novaCert.setVeterinario(vet);
                    novaCert.setEspecialidade(esp);

                    repositorioDeCertificacao.salvar(novaCert);

                    break;

                case 2:
                    System.out.println("--- Lista de Todas as Certificações ---");
                    List<Certificacao> certs = repositorioDeCertificacao.ListarCertificacao();

                    if (certs == null || certs.isEmpty()) {
                        System.out.println("Nenhuma certificação cadastrada.");
                    } else {
                        for (Certificacao cert : certs) {
                            System.out.println("--------------------");
                            System.out.println("Nº Registro: " + cert.getNumeroRegistro());
                            System.out.println("Veterinário: " + (cert.getVeterinario() != null ? cert.getVeterinario().getNome() : "N/A"));
                            System.out.println("Especialidade: " + (cert.getEspecialidade() != null ? cert.getEspecialidade().getNome() : "N/A"));
                            System.out.println("Instituição: " + cert.getInstituicaoCertificadora());
                            System.out.println("Data: " + cert.getDataObtencao());
                        }
                        System.out.println("--------------------");
                    }
                    break;

                case 3:
                    System.out.println("--- Buscar Certificação por Número ---");
                    System.out.print("Digite o Número do Registro: ");
                    String numBusca = entrada.nextLine();

                    Certificacao certEncontrada = repositorioDeCertificacao.BuscarNumeroRegistro(numBusca);

                    if (certEncontrada != null) {
                        System.out.println("Certificação encontrada:");
                        System.out.println("Nº Registro: " + certEncontrada.getNumeroRegistro());
                        System.out.println("Veterinário: " + (certEncontrada.getVeterinario() != null ? certEncontrada.getVeterinario().getNome() : "N/A"));
                        System.out.println("Especialidade: " + (certEncontrada.getEspecialidade() != null ? certEncontrada.getEspecialidade().getNome() : "N/A"));
                        System.out.println("Instituição: " + certEncontrada.getInstituicaoCertificadora());
                        System.out.println("Data: " + certEncontrada.getDataObtencao());
                    } else {
                        System.out.println("Certificação com o registro " + numBusca + " não encontrada.");
                    }
                    break;

                case 4:
                    System.out.println("--- Listar Certificações por Veterinário ---");
                    System.out.print("Digite o CRMV do Veterinário: ");
                    String crmvBusca = entrada.nextLine();

                    List<Certificacao> certsVet = repositorioDeCertificacao.BuscarPorVet(crmvBusca);

                    if (certsVet == null || certsVet.isEmpty()) {
                        System.out.println("Nenhuma certificação encontrada para o : " + crmvBusca);
                    } else {
                        System.out.println("Certificações encontradas para " + crmvBusca + ":");
                        for (Certificacao cert : certsVet) {
                            System.out.println("--------------------");
                            System.out.println("Nº Registro: " + cert.getNumeroRegistro());
                            System.out.println("Especialidade: " + (cert.getEspecialidade() != null ? cert.getEspecialidade().getNome() : "N/A")); //
                            System.out.println("Instituição: " + cert.getInstituicaoCertificadora());
                        }
                        System.out.println("--------------------");
                    }
                    break;

                case 5:
                    System.out.println("--- Excluir Certificação ---");
                    System.out.print("Digite o Número do Registro a excluir: ");
                    String idDel = entrada.nextLine();

                    System.out.println("Tem certeza que deseja excluir esta certificação? (S/N)");
                    String confirmacao = entrada.nextLine();

                    if (confirmacao.equalsIgnoreCase("S")) {
                        repositorioDeCertificacao.deletarCertificacao(idDel); //
                        System.out.println("Certificação deletada (se existia).");
                    } else {
                        System.out.println("Exclusão cancelada.");
                    }
                    break;

                case 0:
                    sair = true;
                    System.out.println("Voltando ao menu principal...");
                    break;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }
    }
}

