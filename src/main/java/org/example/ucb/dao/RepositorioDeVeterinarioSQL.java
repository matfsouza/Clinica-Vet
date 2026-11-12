package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeVeterinario;
import org.example.ucb.model.Veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeVeterinarioSQL implements RepositorioDeVeterinario {

    private final ConexaoBD conexaoBD;

    public RepositorioDeVeterinarioSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Veterinario veterinario) {
        String sql = "INSERT INTO veterinario (crmv, nome, idade, data_Graduacao) VALUES (?, ?, ?, ?)";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, veterinario.getCrmv());
            stmt.setString(2, veterinario.getNome());
            stmt.setInt(3, veterinario.getIdade());
            stmt.setObject(4, veterinario.getDataGraduacao());
            stmt.executeUpdate();
            System.out.println("Veterinário salvo no banco de dados com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao salvar veterinário no banco de dados: " + e.getMessage());
        }
    }

    @Override
    public Veterinario BuscarVet(String crmv) {
        String sql = "SELECT * FROM veterinario WHERE crmv = ?";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, crmv);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar veterinário por CRMV: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Veterinario> ListarVet() {
        List<Veterinario> veterinarios = new ArrayList<>();

        try (Connection conn = conexaoBD.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM veterinario")) {
            while (rs.next()) {
                veterinarios.add(mapear(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar veterinários: " + e.getMessage());
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

            if (stmt.executeUpdate() > 0) {
                System.out.println("Veterinário atualizado com sucesso!");
            } else {
                System.err.println("Nenhum veterinário encontrado com o CRMV informado para atualizar.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar veterinário: " + e.getMessage());
        }
    }

    @Override
    public boolean deletarVet(String crmv) {
        String sql = "DELETE FROM veterinario WHERE crmv = ?";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, crmv);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao deletar veterinário: " + e.getMessage());
            return false;
        }
    }

    private static Veterinario mapear(ResultSet rs) throws SQLException {
        return new Veterinario(rs.getString("crmv"), rs.getString("nome"), rs.getInt("idade"),
                rs.getDate("data_Graduacao").toLocalDate());
    }
}
