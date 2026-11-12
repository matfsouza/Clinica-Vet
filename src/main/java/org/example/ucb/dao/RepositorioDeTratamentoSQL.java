package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeTratamento;
import org.example.ucb.model.Tratamento;
import org.example.ucb.model.Consulta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeTratamentoSQL implements RepositorioDeTratamento {

    private final ConexaoBD conexaoBD;

    public RepositorioDeTratamentoSQL() {
        this(new ConexaoMySQL());
    }

    public RepositorioDeTratamentoSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Tratamento tratamento) {
        String sql = "INSERT INTO tratamento (antibiotico, id_consulta, descricao_tratamento) VALUES (?, ?, ?)";
        Connection conexao = null;
        PreparedStatement stmt = null;

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setBoolean(1, tratamento.isAntibiotico());
            stmt.setInt(2, tratamento.getConsulta().getid());
            stmt.setString(3, tratamento.getDescricao());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tratamento.setId(generatedKeys.getInt(1));
                        System.out.println("Tratamento salvo com sucesso! ID: " + tratamento.getId());
                    } else {
                        System.err.println("Falha ao obter o ID do tratamento após salvar.");
                    }
                }
            } else {
                System.err.println("Nenhuma linha afetada ao salvar tratamento.");
            }

        } catch (Exception e) {
            System.err.println("Erro ao salvar tratamento: " + e.getMessage());

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
    public Tratamento BuscarTratamento(int id) {
        String sql = "SELECT * FROM tratamento WHERE id = ?";
        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultado = null;
        Tratamento tratamento = null;

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            resultado = stmt.executeQuery();

            if (resultado.next()) {
                int tratamentoId = resultado.getInt("id");
                boolean antibiotico = resultado.getBoolean("antibiotico");
                String descricao = resultado.getString("descricao_tratamento");
                int idConsulta = resultado.getInt("id_consulta");

                Consulta consulta = new Consulta();
                consulta.setid(idConsulta);

                tratamento = new Tratamento(tratamentoId, descricao, antibiotico, consulta);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar tratamento por ID: " + e.getMessage());

        } finally {
            try {
                if (resultado != null) resultado.close();
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return tratamento;
    }

    @Override
    public List<Tratamento> ListarTratamento() {
        String sql = "SELECT * FROM tratamento";
        Connection conexao = null;
        Statement stmt = null;
        ResultSet resultado = null;
        List<Tratamento> tratamentos = new ArrayList<>();

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.createStatement();
            resultado = stmt.executeQuery(sql);

            while (resultado.next()) {
                int tratamentoId = resultado.getInt("id");
                boolean antibiotico = resultado.getBoolean("antibiotico");
                String descricao = resultado.getString("descricao_tratamento");
                int idConsulta = resultado.getInt("id_consulta");

                Consulta consulta = new Consulta();
                consulta.setid(idConsulta);

                Tratamento tratamento = new Tratamento(tratamentoId, descricao, antibiotico, consulta);
                tratamentos.add(tratamento);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar tratamentos: " + e.getMessage());

        } finally {
            try {
                if (resultado != null) resultado.close();
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return tratamentos;
    }


    @Override
    public List<Tratamento> BuscarPorConsulta(int idConsulta) {
        String sql = "SELECT * FROM tratamento WHERE id_consulta = ?";
        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultado = null;
        List<Tratamento> tratamentos = new ArrayList<>();

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idConsulta);
            resultado = stmt.executeQuery();

            Consulta consulta = new Consulta();
            consulta.setid(idConsulta);

            while (resultado.next()) {
                int tratamentoId = resultado.getInt("id");
                boolean antibiotico = resultado.getBoolean("antibiotico");
                String descricao = resultado.getString("descricao_tratamento");

                Tratamento tratamento = new Tratamento(tratamentoId, descricao, antibiotico, consulta);
                tratamentos.add(tratamento);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar tratamentos por consulta.html: " + e.getMessage());

        } finally {
            try {
                if (resultado != null) resultado.close();
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return tratamentos;
    }

    @Override
    public void atualizarTratamento(Tratamento tratamento) {
        String sql = "UPDATE tratamento SET antibiotico = ?, id_consulta = ?, descricao_tratamento = ? WHERE id = ?";
        Connection conexao = null;
        PreparedStatement stmt = null;

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);

            stmt.setBoolean(1, tratamento.isAntibiotico());
            stmt.setInt(2, tratamento.getConsulta().getid());
            stmt.setString(3, tratamento.getDescricao());
            stmt.setInt(4, tratamento.getId());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Tratamento ID " + tratamento.getId() + " atualizado com sucesso!");
            } else {
                System.err.println("Nenhum tratamento encontrado com o ID " + tratamento.getId() + " para atualizar.");
            }

        } catch (Exception e) {
            System.err.println("Erro ao atualizar tratamento: " + e.getMessage());

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
    public boolean deletarTratamento(int id) {
        String sql = "DELETE FROM tratamento WHERE id = ?";
        Connection conexao = null;
        PreparedStatement stmt = null;
        boolean sucesso = false;

        try {
            conexao = conexaoBD.obterConexao();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Tratamento ID " + id + " deletado com sucesso.");
                sucesso = true;
            } else {
                System.err.println("Nenhum tratamento encontrado com o ID " + id + " para deletar.");
            }

        } catch (Exception e) {
            System.err.println("Erro ao deletar tratamento: " + e.getMessage());

        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexao != null) conexaoBD.fecharConexao(conexao);
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return sucesso;
    }
}