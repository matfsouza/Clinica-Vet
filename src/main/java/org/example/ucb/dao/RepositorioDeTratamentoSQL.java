package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeTratamento;
import org.example.ucb.model.Consulta;
import org.example.ucb.model.Tratamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeTratamentoSQL implements RepositorioDeTratamento {

    private final ConexaoBD conexaoBD;

    public RepositorioDeTratamentoSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Tratamento tratamento) {
        String sql = "INSERT INTO tratamento (antibiotico, id_consulta, descricao_tratamento) VALUES (?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, tratamento.isAntibiotico());
            stmt.setInt(2, tratamento.getConsulta().getid());
            stmt.setString(3, tratamento.getDescricao());

            if (stmt.executeUpdate() == 0) {
                System.err.println("Nenhuma linha afetada ao salvar tratamento.");
                return;
            }
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    tratamento.setId(chaves.getInt(1));
                    System.out.println("Tratamento salvo com sucesso! ID: " + tratamento.getId());
                } else {
                    System.err.println("Falha ao obter o ID do tratamento após salvar.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar tratamento: " + e.getMessage());
        }
    }

    @Override
    public Tratamento BuscarTratamento(int id) {
        List<Tratamento> encontrados = consultar("SELECT * FROM tratamento WHERE id = ?", id,
                "Erro ao buscar tratamento por ID: ");
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    @Override
    public List<Tratamento> ListarTratamento() {
        return consultar("SELECT * FROM tratamento", null, "Erro ao listar tratamentos: ");
    }

    @Override
    public List<Tratamento> BuscarPorConsulta(int idConsulta) {
        return consultar("SELECT * FROM tratamento WHERE id_consulta = ?", idConsulta,
                "Erro ao buscar tratamentos por consulta: ");
    }

    @Override
    public void atualizarTratamento(Tratamento tratamento) {
        String sql = "UPDATE tratamento SET antibiotico = ?, id_consulta = ?, descricao_tratamento = ? WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setBoolean(1, tratamento.isAntibiotico());
            stmt.setInt(2, tratamento.getConsulta().getid());
            stmt.setString(3, tratamento.getDescricao());
            stmt.setInt(4, tratamento.getId());

            if (stmt.executeUpdate() > 0) {
                System.out.println("Tratamento ID " + tratamento.getId() + " atualizado com sucesso!");
            } else {
                System.err.println("Nenhum tratamento encontrado com o ID " + tratamento.getId() + " para atualizar.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar tratamento: " + e.getMessage());
        }
    }

    @Override
    public boolean deletarTratamento(int id) {
        String sql = "DELETE FROM tratamento WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() > 0) {
                System.out.println("Tratamento ID " + id + " deletado com sucesso.");
                return true;
            }
            System.err.println("Nenhum tratamento encontrado com o ID " + id + " para deletar.");
        } catch (Exception e) {
            System.err.println("Erro ao deletar tratamento: " + e.getMessage());
        }
        return false;
    }

    /** Executa um SELECT de tratamentos com no máximo um parâmetro inteiro e converte cada linha. */
    private List<Tratamento> consultar(String sql, Integer parametro, String mensagemDeErro) {
        List<Tratamento> tratamentos = new ArrayList<>();

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setInt(1, parametro);
            }
            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    tratamentos.add(mapear(resultado));
                }
            }
        } catch (Exception e) {
            System.err.println(mensagemDeErro + e.getMessage());
        }
        return tratamentos;
    }

    private static Tratamento mapear(ResultSet resultado) throws SQLException {
        Consulta consulta = new Consulta();
        consulta.setid(resultado.getInt("id_consulta"));
        return new Tratamento(resultado.getInt("id"), resultado.getString("descricao_tratamento"),
                resultado.getBoolean("antibiotico"), consulta);
    }
}
