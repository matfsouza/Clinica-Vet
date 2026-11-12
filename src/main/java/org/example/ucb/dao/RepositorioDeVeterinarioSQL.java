package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeVeterinario;
import org.example.ucb.model.Veterinario;
import org.example.ucb.model.Certificacao;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class RepositorioDeVeterinarioSQL implements RepositorioDeVeterinario {

    private final ConexaoBD conexaoBD;

    public RepositorioDeVeterinarioSQL() {
        this(new ConexaoMySQL());
    }

    public RepositorioDeVeterinarioSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Veterinario veterinario) {
        String sql = "INSERT INTO veterinario (crmv, nome, idade, data_Graduacao) VALUES (?, ?, ?, ?)";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, veterinario.getCrmv());
            stmt.setString(2, veterinario.getNome());
            stmt.setInt(3, veterinario.getIdade());
            stmt.setObject(4, veterinario.getDataGraduacao());

            stmt.executeUpdate();
            System.out.println("Veterinário salvo no banco de dados com sucesso!");

        }
        catch (SQLException e) {
            System.err.println("Erro ao salvar veterinário no banco de dados.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Veterinario BuscarVet(String crmv) {
        String sql = "SELECT * FROM veterinario WHERE crmv = ?";
        Veterinario vetEncontrado = null;

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, crmv);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    int idade = rs.getInt("idade");
                    java.time.LocalDate dataGraduacao = rs.getDate("data_Graduacao").toLocalDate();

                    vetEncontrado = new Veterinario(crmv, nome, idade, dataGraduacao);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veterinário por CRMV.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return vetEncontrado;
    }

    @Override
    public List<Veterinario> ListarVet() {
    String sql = "SELECT * FROM veterinario";
    List<Veterinario> veterinarios = new ArrayList<>();

        try (Connection conn = conexaoBD.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            String crmv = rs.getString("crmv");
            String nome = rs.getString("nome");
            int idade = rs.getInt("idade");
            java.time.LocalDate dataGraduacao = rs.getDate("data_Graduacao").toLocalDate();

            veterinarios.add(new Veterinario(crmv, nome, idade, dataGraduacao));
        }
    } catch (SQLException e) {
        System.err.println("Erro ao listar veterinários.");
        e.printStackTrace();
    } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return veterinarios;
    }

    @Override
    public List<Veterinario> BuscarPorCertificacao(int numeroregistro) {
        return List.of();
    }

    @Override
    public void atualizar(Veterinario veterinario) {
        String sql = "UPDATE veterinario SET nome = ?, idade = ?, data_Graduacao = ? WHERE crmv = ?";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, veterinario.getNome());
            stmt.setInt(2, veterinario.getIdade());
            stmt.setObject(3, veterinario.getDataGraduacao());
            stmt.setString(4, veterinario.getCrmv());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Veterinário atualizado com sucesso!");
            } else {
                System.err.println("Nenhum veterinário encontrado com o CRMV informado para atualizar.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar veterinário.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deletarVet(String crmv) {
        String sql = "DELETE FROM veterinario WHERE crmv = ?";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, crmv);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar veterinário.");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
