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

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, especialidade.getNome());
            stmt.executeUpdate();
            System.out.println("Especialidade '" + especialidade.getNome() + "' salva com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao salvar especialidade: " + e.getMessage());
        }
    }

    @Override
    public Especialidade BuscarEspecialidade(int id) {
        String sql = "SELECT * FROM Especialidade WHERE IDEspecialidade = ?";
        List<Especialidade> encontradas = consultar(sql, id, "Erro ao buscar especialidade por ID: ");
        return encontradas.isEmpty() ? null : encontradas.get(0);
    }

    @Override
    public List<Especialidade> ListarEspecialidade() {
        return consultar("SELECT * FROM Especialidade", null, "Erro ao listar especialidades: ");
    }

    @Override
    public List<Especialidade> BuscarEspPorVet(String crmv) {
        String sql = "SELECT E.* FROM Especialidade E " +
                     "INNER JOIN Certificacao C ON E.IDespecialidade = C.ID_especialidade " +
                     "WHERE C.CRMV_certif = ?";
        return consultar(sql, crmv, "Erro ao buscar especialidades por veterinário: ");
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
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Falha ao deletar especialidade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Executa um SELECT de especialidades com no máximo um parâmetro e converte cada linha.
     * Concentra o bloco de abrir conexão, preparar, ler e fechar que antes se repetia em três métodos.
     */
    private List<Especialidade> consultar(String sql, Object parametro, String mensagemDeErro) {
        List<Especialidade> especialidades = new ArrayList<>();

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setObject(1, parametro);
            }
            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    especialidades.add(mapear(resultado));
                }
            }
        } catch (Exception e) {
            System.err.println(mensagemDeErro + e.getMessage());
        }
        return especialidades;
    }

    private static Especialidade mapear(ResultSet resultado) throws SQLException {
        return new Especialidade(resultado.getInt("IDespecialidade"), resultado.getString("nome"));
    }
}
