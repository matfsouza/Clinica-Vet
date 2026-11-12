package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeDono;
import org.example.ucb.model.Dono;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeDonoSQL implements RepositorioDeDono {

    private final ConexaoBD conexaoBD;

    public RepositorioDeDonoSQL() {
        this(new ConexaoMySQL());
    }

    public RepositorioDeDonoSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Dono dono) {
        String sql = "INSERT INTO dono (CPF, Nome, Endereco, data_nasc) VALUES (?, ?, ?, ?)";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dono.getCPF());
            stmt.setString(2, dono.getNome());
            stmt.setString(3, dono.getEndereco());
            stmt.setObject(4, dono.getDataNascimento());
            stmt.executeUpdate();
            System.out.println("Dono salvo no banco de dados com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao salvar dono no banco de dados: " + e.getMessage());
        }
    }


    @Override
    public Dono BuscarPorCPF(String cpf) {
        String sql = "SELECT * FROM dono WHERE CPF = ?";
        Dono donoEncontrado = null;

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String cpfEncontrado = rs.getString("CPF");
                    String nome = rs.getString("Nome");
                    String endereco = rs.getString("Endereco");
                    LocalDate dataNascimento = rs.getDate("data_nasc").toLocalDate();

                    donoEncontrado = new Dono(cpfEncontrado, dataNascimento, endereco, nome);
                }
            }
        } catch (Exception e) {
             System.err.println("Erro ao buscar dono por CPF: " + e.getMessage());
        }
        return donoEncontrado;
    }

    @Override
    public List<Dono> ListarDono() {
        String sql = "SELECT * FROM dono";
        List<Dono> donos = new ArrayList<>();

        try (Connection conn = conexaoBD.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String cpf = rs.getString("CPF");
                String nome = rs.getString("Nome");
                String endereco = rs.getString("Endereco");
                LocalDate dataNascimento = rs.getDate("data_nasc").toLocalDate();

                donos.add(new Dono(cpf, dataNascimento, endereco, nome));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar donos: " + e.getMessage());
        }
        return donos;
    }

    @Override
    public List<Dono> BuscarPorAnimal(int id) {
        System.out.println("Atenção: Método BuscarPorAnimal ainda não implementado!");
        return new ArrayList<>();
    }

    @Override
    public void atualizar(Dono dono) {
        String sql = "UPDATE dono SET Nome = ?, Endereco = ?, data_nasc = ? WHERE CPF = ?";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dono.getNome());
            stmt.setString(2, dono.getEndereco());
            stmt.setObject(3, dono.getDataNascimento());
            stmt.setString(4, dono.getCPF());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Dono atualizado com sucesso!");
            } else {
                System.err.println("Nenhum dono encontrado com o CPF " + dono.getCPF() + " para atualizar.");
            }

        } catch (Exception e) {
             System.err.println("Erro ao atualizar dono: " + e.getMessage());
        }
    }

    @Override
    public boolean deletarDono(String cpf) {
        String sql = "DELETE FROM dono WHERE CPF = ?";

        try (Connection conn = conexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            int linhasAfetadas = stmt.executeUpdate();
            if(linhasAfetadas > 0) {
                System.out.println("Dono com CPF " + cpf + " deletado com sucesso.");
            } else {
                 System.err.println("Nenhum dono encontrado com o CPF " + cpf + " para deletar.");
            }
            return linhasAfetadas > 0;

        } catch (Exception e) {
            System.err.println("Erro ao deletar dono: " + e.getMessage());
            return false;
        }
    }
}
