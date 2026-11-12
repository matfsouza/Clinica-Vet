package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeEspecialidade;
import org.example.ucb.model.Especialidade;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeEspecialidadeSQL implements RepositorioDeEspecialidade {

    private final ConexaoBD conexaoBD;

    public RepositorioDeEspecialidadeSQL() {
        this(new ConexaoMySQL());
    }

    public RepositorioDeEspecialidadeSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Especialidade especialidade) {

        String sql = "INSERT INTO Especialidade (nome) VALUES (?)";

        Connection conexao = null;
        PreparedStatement stmt = null;

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);

            stmt.setString(1, especialidade.getNome());

            stmt.executeUpdate();

            System.out.println("Especialidade '" + especialidade.getNome() + "' salva com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao salvar especialidade: " + e.getMessage());
        
        } finally {
            
            try {
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    @Override
    public Especialidade BuscarEspecialidade(int id) {

        String sql = "SELECT * FROM Especialidade WHERE IDEspecialidade = ?";
        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultado = null;

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id); 
            resultado = stmt.executeQuery();

            if (resultado.next()) {

                int especialidadeId = resultado.getInt("IDespecialidade");
                String nome = resultado.getString("nome");
                return new Especialidade(especialidadeId, nome);
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar especialidade por ID: " + e.getMessage());
        
        } finally {
            try {
                if (resultado != null) resultado.close();
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<Especialidade> ListarEspecialidade() {
        String sql = "SELECT * FROM Especialidade";
        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultado = null;
        List<Especialidade> especialidades = new ArrayList<>();

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);
            resultado = stmt.executeQuery();

            while (resultado.next()) {

                int especialidadeId = resultado.getInt("IDespecialidade");
                String nome = resultado.getString("nome");

                Especialidade especialidade = new Especialidade(especialidadeId, nome);
                especialidades.add(especialidade);
            }

        } catch (Exception e) {
            System.err.println("Erro ao listar especialidades: " + e.getMessage());
        
        } finally {
            try {
                if (resultado != null) resultado.close();
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return especialidades;
    }

     @Override
    public List<Especialidade> BuscarEspPorVet(String crmv) {
        String sql = "SELECT E.* FROM Especialidade E " +
                     "INNER JOIN Certificacao C ON E.IDespecialidade = C.ID_especialidade " +
                     "WHERE C.CRMV_certif = ?";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultado = null;
        List<Especialidade> especialidades = new ArrayList<>();

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, crmv); 
            resultado = stmt.executeQuery();
            while (resultado.next()) {
                int especialidadeId = resultado.getInt("IDespecialidade");
                String nome = resultado.getString("nome");

                Especialidade especialidade = new Especialidade(especialidadeId, nome);
                especialidades.add(especialidade);
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar especialidades por veterinário: " + e.getMessage());

        } finally {
            try {
                if (resultado != null) resultado.close();
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return especialidades;
    }

    @Override
    public void atualizarEspecialidade(Especialidade especialidade) {
        String sql = "UPDATE Especialidade SET nome = ? WHERE IDespecialidade = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, especialidade.getNome());
            stmt.setInt(2, especialidade.getId());

            stmt.executeUpdate();

            System.out.println("Especialidade ID : " + especialidade.getId() + " atualizada com sucesso!!");

        } catch (Exception e) {
            System.err.println("Falha ao atualizar especialidade: " + e.getMessage());
        }
    }

    @Override
    public boolean deletarEspecialidade(int id) {
        String sql = "DELETE FROM Especialidade WHERE IDespecialidade = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (Exception e) {
            System.err.println("Falha ao deletar especialidade: " + e.getMessage());
            return false;
        }
    }
}
