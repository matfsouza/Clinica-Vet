package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeAnimal;
import org.example.ucb.control.RepositorioDeDono;
import org.example.ucb.model.Animal;
import org.example.ucb.model.Exotico;
import org.example.ucb.model.Pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class RepositorioDeAnimalSQL implements RepositorioDeAnimal {

    private static final String SELECT_ANIMAL =
            "SELECT a.*, p.RFID, e.Nota_Fiscal, e.RFIDEX FROM Animal a "
            + "LEFT JOIN Pet p ON a.ID = p.animal_ID LEFT JOIN Exotico e ON a.ID = e.animal_ID";

    private final ConexaoBD conexaoBD;

    public RepositorioDeAnimalSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Animal animal) {
        String sqlAnimal = "INSERT INTO Animal (Especie, CPF_Dono, Nome, Idade, Porte) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao()) {

            try (PreparedStatement stmtAnimal = conexao.prepareStatement(sqlAnimal, Statement.RETURN_GENERATED_KEYS)) {
                stmtAnimal.setString(1, animal.getEspecie());
                stmtAnimal.setString(2, animal.getDono().getCPF());
                stmtAnimal.setString(3, animal.getNome());
                stmtAnimal.setInt(4, animal.getIdade());
                stmtAnimal.setString(5, animal.getPorte());
                stmtAnimal.executeUpdate();

                try (ResultSet chaves = stmtAnimal.getGeneratedKeys()) {
                    if (chaves.next()) {
                        animal.setId(chaves.getInt(1));
                    }
                }
            }

            if (animal instanceof Pet pet) {
                String sqlPet = "INSERT INTO Pet (animal_ID, RFID) VALUES (?, ?)";
                try (PreparedStatement stmtPet = conexao.prepareStatement(sqlPet)) {
                    stmtPet.setInt(1, pet.getId());
                    stmtPet.setString(2, pet.getrfid());
                    stmtPet.executeUpdate();
                }
            } else if (animal instanceof Exotico exotico) {
                String sqlExotico = "INSERT INTO Exotico (animal_ID, Nota_Fiscal, RFIDEX) VALUES (?, ?, ?)";
                try (PreparedStatement stmtExotico = conexao.prepareStatement(sqlExotico)) {
                    stmtExotico.setInt(1, exotico.getId());
                    stmtExotico.setString(2, exotico.getNotaFiscal());
                    stmtExotico.setString(3, exotico.getRfidex());
                    stmtExotico.executeUpdate();
                }
            }

            System.out.println("Animal\n ID: " + animal.getId() + "Espécie: " + animal.getEspecie() + " Cpf do Dono: " + animal.getDono().getCPF() + " Nome: " + animal.getNome() + " Idade: " + animal.getIdade() + " Porte: " + animal.getPorte() + "\ncadastrado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar animal: " + e.getMessage());
        }
    }

    @Override
    public Animal BuscarPorId(int id) {
        List<Animal> encontrados = consultar(SELECT_ANIMAL + " WHERE a.ID = ?", id, false,
                "Erro ao buscar o animal pelo ID: ");
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    @Override
    public List<Animal> ListarTodos() {
        return consultar(SELECT_ANIMAL, null, true, "Erro na listagem de animais: ");
    }

    @Override
    public List<Animal> BuscarPorDono(String cpfDono) {
        return consultar(SELECT_ANIMAL + " WHERE a.CPF_dono = ?", cpfDono, false,
                "Erro ao buscar o animal pelo Dono: ");
    }

    @Override
    public boolean deletarAnimal(int id) {
        String sqlSelect = "SELECT p.animal_ID as petId, e.animal_ID as exoticoId FROM Animal a " +
                "LEFT JOIN Pet p ON a.ID = p.animal_ID " +
                "LEFT JOIN Exotico e ON a.ID = e.animal_ID " +
                "WHERE a.ID = ?";

        try (Connection conexao = conexaoBD.obterConexao()) {
            try (PreparedStatement stmtSelect = conexao.prepareStatement(sqlSelect)) {
                stmtSelect.setInt(1, id);
                try (ResultSet rs = stmtSelect.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getObject("petID") != null) {
                            executarUpdate(conexao, "DELETE FROM Pet WHERE animal_ID = ?", id);
                        } else if (rs.getObject("exoticoID") != null) {
                            executarUpdate(conexao, "DELETE FROM Exotico WHERE animal_ID = ?", id);
                        }
                    }
                }
            }
            return executarUpdate(conexao, "DELETE FROM Animal WHERE ID = ?", id) > 0;
        } catch (Exception e) {
            System.err.println("Erro ao deletar o animal: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void atualizar(Animal animal) {
        String sqlAnimal = "UPDATE Animal SET Nome = ?,Especie = ?, Idade = ?, Porte = ? WHERE ID = ?";
        try (Connection conexao = conexaoBD.obterConexao()) {
            try (PreparedStatement stmtAnimal = conexao.prepareStatement(sqlAnimal)) {
                stmtAnimal.setString(1, animal.getNome());
                stmtAnimal.setString(2, animal.getEspecie());
                stmtAnimal.setInt(3, animal.getIdade());
                stmtAnimal.setString(4, animal.getPorte());
                stmtAnimal.setInt(5, animal.getId());
                stmtAnimal.executeUpdate();
            }

            if (animal instanceof Pet pet) {
                String sqlPet = "UPDATE Pet SET RFID = ? WHERE animal_ID = ?";
                try (PreparedStatement stmtPet = conexao.prepareStatement(sqlPet)) {
                    stmtPet.setString(1, pet.getrfid());
                    stmtPet.setInt(2, pet.getId());
                    stmtPet.executeUpdate();
                }
            } else if (animal instanceof Exotico exotico) {
                String sqlExotico = "UPDATE Exotico SET Nota_Fiscal = ?, RFIDEX = ? WHERE animal_ID = ?";
                try (PreparedStatement stmtExotico = conexao.prepareStatement(sqlExotico)) {
                    stmtExotico.setString(1, exotico.getNotaFiscal());
                    stmtExotico.setString(2, exotico.getRfidex());
                    stmtExotico.setInt(3, exotico.getId());
                    stmtExotico.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o animal: " + e.getMessage());
        }
    }

    /**
     * Executa um SELECT de animais e monta Pet ou Exotico conforme o subtipo gravado.
     * Antes, esse mapeamento aparecia copiado em BuscarPorId, ListarTodos e BuscarPorDono.
     */
    private List<Animal> consultar(String sql, Object parametro, boolean carregarDono, String mensagemDeErro) {
        List<Animal> animais = new ArrayList<>();
        RepositorioDeDono repositorioDonos = new RepositorioDeDonoSQL(conexaoBD);

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setObject(1, parametro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Animal animal = mapear(rs);
                    if (animal == null) {
                        continue;
                    }
                    if (carregarDono) {
                        animal.setDono(repositorioDonos.BuscarPorCPF(rs.getString("CPF_Dono")));
                    }
                    animais.add(animal);
                }
            }
        } catch (Exception e) {
            System.err.println(mensagemDeErro + e.getMessage());
        }
        return animais;
    }

    /** Converte a linha atual em Pet ou Exotico; devolve null se o animal não tem subtipo gravado. */
    private static Animal mapear(ResultSet rs) throws SQLException {
        Animal animal;
        String rfidPet = rs.getString("RFID");
        String notaFiscal = rs.getString("Nota_Fiscal");

        if (rfidPet != null) {
            Pet pet = new Pet();
            pet.setrfid(rfidPet);
            animal = pet;
        } else if (notaFiscal != null) {
            Exotico exotico = new Exotico();
            exotico.setNotaFiscal(notaFiscal);
            exotico.setRfidex(rs.getString("RFIDEX"));
            animal = exotico;
        } else {
            return null;
        }

        animal.setId(rs.getInt("ID"));
        animal.setNome(rs.getString("Nome"));
        animal.setEspecie(rs.getString("Especie"));
        animal.setIdade(rs.getInt("Idade"));
        animal.setPorte(rs.getString("Porte"));
        return animal;
    }

    private static int executarUpdate(Connection conexao, String sql, int id) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate();
        }
    }
}
