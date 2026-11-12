package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeConsulta;
import org.example.ucb.model.Animal;
import org.example.ucb.model.Consulta;
import org.example.ucb.model.Pet;
import org.example.ucb.model.Veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeConsultaSQL implements RepositorioDeConsulta {

    /** Consulta com o nome do animal e do veterinário. A tabela é "consulta" (o commit 6eadb4f trocou por "consulta.html"). */
    private static final String SELECT_CONSULTA =
            "SELECT co.*, a.Nome as animal_nome, v.nome as vet_nome FROM consulta co "
            + "INNER JOIN Animal a ON co.id_animal = a.ID "
            + "INNER JOIN veterinario v ON co.CRMV_veterinario = v.CRMV";

    private final ConexaoBD conexaoBD;

    public RepositorioDeConsultaSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Consulta consulta) {
        String sql = "INSERT INTO consulta (diagnostico, id_animal, CRMV_veterinario) VALUES (?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, consulta.getdiagnostico());
            stmt.setInt(2, consulta.getanimal().getId());
            stmt.setString(3, consulta.getveterinario().getCrmv());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        consulta.setid(chaves.getInt(1));
                    }
                }
            }
            System.out.println("Consulta salva com sucesso! ID: " + consulta.getid());
        } catch (Exception e) {
            System.err.println("Erro ao salvar consulta: " + e.getMessage());
        }
    }

    @Override
    public Consulta BuscarConsulta(int id) {
        List<Consulta> encontradas = consultar(SELECT_CONSULTA + " WHERE co.id = ?", id,
                "Erro ao buscar consulta por ID: ");
        return encontradas.isEmpty() ? null : encontradas.get(0);
    }

    @Override
    public List<Consulta> ListarConsulta() {
        return consultar(SELECT_CONSULTA, null, "Erro ao listar as consultas: ");
    }

    @Override
    public List<Consulta> BuscarPorAnimal(int id) {
        return consultar(SELECT_CONSULTA + " WHERE co.id_animal = ?", id,
                "Erro ao buscar consulta por animal: ");
    }

    @Override
    public void atualizarConsulta(Consulta consulta) {
        String sql = "UPDATE consulta SET diagnostico = ?, id_animal = ?, CRMV_veterinario = ? WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, consulta.getdiagnostico());
            stmt.setInt(2, consulta.getanimal().getId());
            stmt.setString(3, consulta.getveterinario().getCrmv());
            stmt.setInt(4, consulta.getid());
            stmt.executeUpdate();
            System.out.println("Consulta ID : " + consulta.getid() + " atualizada com sucesso!!");
        } catch (Exception e) {
            System.err.println("Falha ao atualizar consulta: " + e.getMessage());
        }
    }

    @Override
    public boolean deletarConsulta(int id) {
        String sql = "DELETE FROM consulta WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao deletar consulta: " + e.getMessage());
            return false;
        }
    }

    /** Executa um SELECT de consultas com no máximo um parâmetro inteiro e converte cada linha. */
    private List<Consulta> consultar(String sql, Integer parametro, String mensagemDeErro) {
        List<Consulta> consultas = new ArrayList<>();

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setInt(1, parametro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultas.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.err.println(mensagemDeErro + e.getMessage());
        }
        return consultas;
    }

    private static Consulta mapear(ResultSet rs) throws SQLException {
        Animal animal = new Pet();
        animal.setId(rs.getInt("id_animal"));
        animal.setNome(rs.getString("animal_nome"));

        Veterinario veterinario = new Veterinario();
        veterinario.setCrmv(rs.getString("CRMV_veterinario"));
        veterinario.setNome(rs.getString("vet_nome"));

        return new Consulta(rs.getInt("id"), rs.getString("diagnostico"), animal, veterinario);
    }
}
