package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeConsulta;
import org.example.ucb.model.Animal;
import org.example.ucb.model.Consulta;
import org.example.ucb.model.Pet;
import org.example.ucb.model.Veterinario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeConsultaSQL implements RepositorioDeConsulta {

    private final ConexaoBD conexaoBD;

    public RepositorioDeConsultaSQL() {
        this(new ConexaoMySQL());
    }

    public RepositorioDeConsultaSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Consulta consulta) {
            String sql = "INSERT INTO consulta.html (diagnostico, id_animal, CRMV_veterinario) VALUES (?, ?, ?)";

            try (Connection conexao = conexaoBD.obterConexao();
                 PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, consulta.getdiagnostico());
                stmt.setInt(2, consulta.getanimal().getId());
                stmt.setString(3, consulta.getveterinario().getCrmv());

                int linhasAfetadas = stmt.executeUpdate();
                if(linhasAfetadas > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if(generatedKeys.next()) {
                            consulta.setid(generatedKeys.getInt(1));
                        }
                    }
                }
                System.out.println("Consulta salva com sucesso! ID: " + consulta.getid());
            } catch (Exception e) {
                System.err.println("Erro ao salvar consulta.html: " + e.getMessage());
            }
    }

    @Override
    public Consulta BuscarConsulta(int id) {
        String sql = "SELECT co.*, a.Nome as animal_nome, v.nome as vet_nome FROM consulta.html co " +
                "INNER JOIN Animal a ON co.id_animal = a.ID " +
                "INNER JOIN veterinario v ON co.CRMV_veterinario = v.CRMV WHERE co.id = ?";

        Consulta consulta = null;

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try(ResultSet rs= stmt.executeQuery()) {
                if(rs.next()) {
                    consulta = new Consulta();
                    Veterinario veterinario = new Veterinario();

                    Animal animalDaConsulta = new Pet();
                    animalDaConsulta.setId(rs.getInt("id_animal"));
                    animalDaConsulta.setNome(rs.getString("animal_nome"));

                    veterinario.setCrmv(rs.getString("CRMV_veterinario"));
                    veterinario.setNome(rs.getString("vet_nome"));

                consulta.setid(rs.getInt("id"));
                consulta.setdiagnostico(rs.getString("diagnostico"));

                consulta.setanimal(animalDaConsulta);
                consulta.setveterinario(veterinario);
                }
            }
        } catch (Exception e) {
        System.err.println("Erro ao buscar consulta.html por ID: " + e.getMessage());
        }
        return consulta;
    }

    @Override
    public List<Consulta> ListarConsulta() {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT co.*, a.Nome as animal_nome, v.nome as vet_nome FROM consulta.html co INNER JOIN Animal a ON co.id_animal = a.ID INNER JOIN veterinario v ON co.CRMV_veterinario = v.CRMV";

        try (Connection conexao = conexaoBD.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Consulta consulta = new Consulta();
                Veterinario veterinario = new Veterinario();

                Animal animalDaConsulta = new Pet();

                animalDaConsulta.setId(rs.getInt("id_animal"));
                animalDaConsulta.setNome(rs.getString("animal_nome"));

                veterinario.setCrmv(rs.getString("CRMV_veterinario"));
                veterinario.setNome(rs.getString("vet_nome"));

                consulta.setid(rs.getInt("id"));
                consulta.setdiagnostico(rs.getString("diagnostico"));

                consulta.setanimal(animalDaConsulta);
                consulta.setveterinario(veterinario);

                consultas.add(consulta);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar as consultas: " + e.getMessage());
        }
        return consultas;
    }

    @Override
     public List<Consulta> BuscarPorAnimal(int id) {
    List<Consulta> consultas = new ArrayList<>();
    String sql = "SELECT co.*, a.Nome as animal_nome, v.nome as vet_nome FROM consulta.html co INNER JOIN Animal a ON co.id_animal = a.ID INNER JOIN veterinario v ON co.CRMV_veterinario = v.CRMV WHERE co.id_animal = ?";

        try (Connection conexao = conexaoBD.obterConexao();
    PreparedStatement stmt = conexao.prepareStatement(sql)) {
        stmt.setInt(1, id);

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Consulta consulta = new Consulta();
                Veterinario veterinario = new Veterinario();
                Animal animal = new Pet();

                animal.setId(rs.getInt("id_animal"));
                animal.setNome(rs.getString("animal_nome"));

                veterinario.setCrmv(rs.getString("CRMV_veterinario"));
                veterinario.setNome(rs.getString("vet_nome"));

                consulta.setid(rs.getInt("id"));
                consulta.setdiagnostico(rs.getString("diagnostico"));

                consulta.setanimal(animal);
                consulta.setveterinario(veterinario);

                consultas.add(consulta);
            }
        }
    } catch (Exception e) {
        System.err.println("Erro ao buscar consulta.html por animal: " + e.getMessage());
    }
        return consultas;
    }
    @Override
    public void atualizarConsulta(Consulta consulta) {
    String sql = "UPDATE consulta.html SET diagnostico = ?, id_animal = ?, CRMV_veterinario = ? WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
    PreparedStatement stmt = conexao.prepareStatement(sql)) {

        stmt.setString(1, consulta.getdiagnostico());
        stmt.setInt(2, consulta.getanimal().getId());
        stmt.setString(3, consulta.getveterinario().getCrmv());
        stmt.setInt(4, consulta.getid());
        stmt.executeUpdate();

        System.out.println("Cosulta ID : " + consulta.getid() + " atualizada com sucesso!!");

    } catch (Exception e) {
        System.err.println("Falaha ao atualizar consulta.html " + e.getMessage());
    }
}

    @Override
    public boolean deletarConsulta(int id) {
    String sql = "DELETE FROM consulta.html WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
    PreparedStatement stmt = conexao.prepareStatement(sql)) {
        stmt.setInt(1, id);

        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0;
    } catch (Exception e) {
        System.err.println("Erro ao deletar consulta.html: " + e.getMessage());
        return false;
        }
    }
}

