package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeCertificacao;
import org.example.ucb.model.Certificacao;
import org.example.ucb.model.Especialidade;
import org.example.ucb.model.Veterinario;

import java.awt.image.AreaAveragingScaleFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class RepositorioDeCertificacaoSQL implements RepositorioDeCertificacao {

    private final ConexaoBD conexaoBD;

    public RepositorioDeCertificacaoSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Certificacao certificacao) {
        String sql = "INSERT INTO Certificacao (NumeroRegistro, DataObtencao, InstituicaoCertificadora, CRMV_certif, ID_especialidade) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, certificacao.getNumeroRegistro());
            stmt.setDate(2, java.sql.Date.valueOf(certificacao.getDataObtencao()));
            stmt.setString(3, certificacao.getInstituicaoCertificadora());
            stmt.setString(4, certificacao.getVeterinario().getCrmv());
            stmt.setInt(5, certificacao.getEspecialidade().getId());


            stmt.executeUpdate();
            System.out.println("Certificacao cadastrada com sucesso!");
        } catch (Exception e) {
            System.err.println("Falha no cadastro da certificação: " + e.getMessage());
        }
    }

    @Override
    public Certificacao BuscarNumeroRegistro(String numeroregistro) {
        String sql = "SELECT c.*, e.nome as especialidade_nome, v.nome as vet_nome FROM Certificacao c INNER JOIN Especialidade e ON c.ID_especialidade = e.IDespecialidade INNER JOIN veterinario v ON c.CRMV_certif = v.CRMV WHERE c.NumeroRegistro = ?";
        Certificacao certificacao = null;

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, numeroregistro);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    certificacao = new Certificacao();
                    Veterinario veterinario = new Veterinario();
                    Especialidade especialidade = new Especialidade();

                    veterinario.setCrmv(rs.getString("CRMV_Certif"));
                    veterinario.setNome(rs.getString("Vet_nome"));

                    especialidade.setId(rs.getInt("ID_especialidade"));
                    especialidade.setNome(rs.getString("especialidade_nome"));

                    certificacao.setNumeroRegistro(rs.getString("NumeroRegistro"));


                    certificacao.setVeterinario(veterinario);
                    certificacao.setEspecialidade(especialidade);
                }
            }
        } catch (Exception e) {
            System.err.println("Falha na busaca da certificacao: " + e.getMessage());
        }
        return certificacao;
    }

    @Override
    public List<Certificacao> ListarCertificacao() {
        String sql = "SELECT c.*, e.nome as especialidade_nome, v.nome as Vet_nome FROM Certificacao c INNER JOIN Especialidade e ON c.ID_especialidade = e.IDespecialidade INNER JOIN Veterinario v ON c.CRMV_certif = v.CRMV";
        List<Certificacao> certificacoes= new ArrayList<>();

        try (Connection conexao = conexaoBD.obterConexao();
             Statement stmt =   conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Certificacao certificacaoDV = new Certificacao();
                Especialidade especialidadeDV = new Especialidade();
                Veterinario veterinarioDV = new Veterinario();

                veterinarioDV.setCrmv(rs.getString("CRMV_certif"));
                veterinarioDV.setNome(rs.getString("Vet_nome"));

                especialidadeDV.setId(rs.getInt("ID_especialidade"));
                veterinarioDV.setNome(rs.getString("especialidade_nome"));

                certificacaoDV.setNumeroRegistro(rs.getString("NumeroRegistro"));
                certificacaoDV.setDataObtencao(rs.getDate("DataObtencao").toLocalDate());
                certificacaoDV.setInstituicaoCertificadora(rs.getString("InstituicaoCertificadora"));

                certificacaoDV.setVeterinario(veterinarioDV);
                certificacaoDV.setEspecialidade(especialidadeDV);

                certificacoes.add(certificacaoDV);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar as certificacoes: " + e.getMessage());
        }
        return certificacoes;
    }

    @Override
    public List<Certificacao> BuscarPorVet (String crmv) {
        List<Certificacao> certificacaoVet = new ArrayList<>();

        String sql = "SELECT c.*, e.nome as especialidade_nome, v.nome as Vet_nome FROM Certificacao c INNER JOIN Especialidade e ON c.ID_especialidade = e.IDespecialidade INNER JOIN Veterinario v ON c.CRMV_certif = v.CRMV WHERE v.CRMV = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, crmv);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Certificacao certificacaoDV = new Certificacao();
                    Especialidade especialidadeDV = new Especialidade();
                    Veterinario veterinarioDV = new Veterinario();

                    veterinarioDV.setCrmv(rs.getString("CRMV_certif"));
                    veterinarioDV.setNome(rs.getString("Vet_nome"));

                    especialidadeDV.setId(rs.getInt("ID_especialidade"));
                    especialidadeDV.setNome(rs.getString("especialidade_nome"));

                    certificacaoDV.setNumeroRegistro(rs.getString("NumeroRegistro"));
                    certificacaoDV.setDataObtencao(rs.getDate("DataObtencao").toLocalDate());
                    certificacaoDV.setInstituicaoCertificadora(rs.getString("InstituicaoCertificadora"));

                    certificacaoDV.setVeterinario(veterinarioDV);
                    certificacaoDV.setEspecialidade(especialidadeDV);

                    certificacaoVet.add(certificacaoDV);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar pelo vet: " + e.getMessage());
        }
        return certificacaoVet;
    }

    @Override
    public boolean deletarCertificacao(String numeroRegistro) {
        String sql = "DELETE FROM Certificacao WHERE NumeroRegistro = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, numeroRegistro);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (Exception e) {
            System.err.println("Erro ao deletar certificacao: " + e.getMessage());
            return false;
        }
    }

}
