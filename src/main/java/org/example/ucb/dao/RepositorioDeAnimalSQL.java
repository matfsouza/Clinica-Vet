package org.example.ucb.dao;

import org.example.ucb.control.RepositorioDeAnimal;
import org.example.ucb.control.RepositorioDeDono;
import org.example.ucb.model.Dono;
import org.example.ucb.model.Animal;
import org.example.ucb.model.Pet;
import org.example.ucb.model.Exotico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class RepositorioDeAnimalSQL implements RepositorioDeAnimal {

    private final ConexaoBD conexaoBD;

    public RepositorioDeAnimalSQL() {
        this(new ConexaoMySQL());
    }

    public RepositorioDeAnimalSQL(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(Animal animal) {
        //"Instrução" para o SQL
        String sqlAnimal = "INSERT INTO Animal (Especie, CPF_Dono, Nome, Idade, Porte) VALUES (?, ?, ?, ?, ?)";
        //Tenta fazer a conexão com o banco
        try (Connection conexao = conexaoBD.obterConexao()) {

            try (PreparedStatement stmtAnimal = conexao.prepareStatement(sqlAnimal, Statement.RETURN_GENERATED_KEYS)) {
                //Inserção dos dados
                stmtAnimal.setString(1, animal.getEspecie());
                stmtAnimal.setString(2, animal.getDono().getCPF());
                stmtAnimal.setString(3, animal.getNome());
                stmtAnimal.setInt(4, animal.getIdade());
                stmtAnimal.setString(5, animal.getPorte());

                stmtAnimal.executeUpdate();

                try (ResultSet generatedKeys = stmtAnimal.getGeneratedKeys()){
                    if(generatedKeys.next()) {
                        animal.setId(generatedKeys.getInt(1));
                    }
                }catch (Exception e) {
                    System.err.println("Falha ao obter o ID do animal, inserção falhou");
                }
            }

            if(animal instanceof Pet){
                String sqlPet = "INSERT INTO Pet (animal_ID, RFID) VALUES (?, ?)";
                try (PreparedStatement stmtPet = conexao.prepareStatement(sqlPet)) {
                    stmtPet.setInt(1, animal.getId());
                    stmtPet.setString(2, ((Pet) animal).getrfid());
                    stmtPet.executeUpdate();
                }
            } else if (animal instanceof Exotico){
                String sqlExotico = "INSERT INTO Exotico (animal_ID, Nota_Fiscal, RFIDEX) VALUES (?, ?, ?)";
                try (PreparedStatement stmtExotico = conexao.prepareStatement(sqlExotico)) {
                    stmtExotico.setInt(1, animal.getId());
                    stmtExotico.setString(2, ((Exotico) animal).getNotaFiscal());
                    stmtExotico.setString(3, ((Exotico) animal).getRfidex());
                    stmtExotico.executeUpdate();
                }

            }

                System.out.println("Animal\n ID: " + animal.getId() + "Espécie: " + animal.getEspecie() + " Cpf do Dono: " + animal.getDono().getCPF() + " Nome: " + animal.getNome() + " Idade: " + animal.getIdade() + " Porte: " + animal.getPorte() + "\ncadastrado com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao cadastrar animal: " + e.getMessage());
            }

    }

    @Override
    public Animal BuscarPorId(int id){
        String sql = "SELECT a.*, p.RFID, e.Nota_Fiscal, e.RFIDEX FROM Animal a LEFT JOIN Pet p ON a.ID = p.animal_ID LEFT JOIN Exotico e ON a.ID = e.animal_ID WHERE a.ID = ?";

        Animal animal = null;

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, id);

            try( ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    String rfidPet = rs.getString(("RFID"));
                    String notaFiscalExotico = rs.getString("Nota_Fiscal");

                    if(rfidPet != null) {
                        animal = new Pet();
                        ((Pet) animal).setrfid(rfidPet);
                    } else if (notaFiscalExotico != null) {
                        animal = new Exotico();
                        ((Exotico) animal).setNotaFiscal(notaFiscalExotico);
                        ((Exotico) animal).setRfidex(rs.getString("RFIDEX"));
                    }

                    if (animal != null) {
                        animal.setId(rs.getInt("ID"));
                        animal.setNome(rs.getString("Nome"));
                        animal.setEspecie(rs.getString("Especie"));
                        animal.setIdade(rs.getInt("Idade"));
                        animal.setPorte(rs.getString("Porte"));
                    }
                }
            }
        } catch (Exception e){
            System.err.println("Erro ao buscar o animal pelo ID");
        }
        return animal;
    }

    @Override
    public List<Animal> ListarTodos() {
        List<Animal> animais = new ArrayList<>();

        String sql = "SELECT a.*, p.RFID, e.Nota_Fiscal, e.RFIDEX FROM Animal a LEFT JOIN Pet p ON a.ID = p.animal_ID LEFT JOIN Exotico e ON a.ID = e.animal_ID";
        RepositorioDeDono repositorioDonos = new RepositorioDeDonoSQL(conexaoBD);

        try (Connection conexao = conexaoBD.obterConexao();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                Animal animal = null;
                String rfidPet = rs.getString("RFID");
                String notaFiscalExotico = rs.getString("Nota_Fiscal");

                if(rfidPet != null){
                    animal = new Pet();
                    ((Pet) animal).setrfid((rfidPet));
                } else if (notaFiscalExotico != null) {
                    animal = new Exotico();
                    ((Exotico) animal).setNotaFiscal(notaFiscalExotico);
                    ((Exotico) animal).setRfidex(rs.getString("RFIDEX"));
                }

                if(animal != null) {
                    animal.setId(rs.getInt("ID"));
                    animal.setNome(rs.getString("Nome"));
                    animal.setEspecie(rs.getString("Especie"));
                    animal.setIdade(rs.getInt("Idade"));
                    animal.setPorte(rs.getString("Porte"));

                    String cpfDono = rs.getString("CPF_Dono");
                    Dono donoAnimal = repositorioDonos.BuscarPorCPF(cpfDono);
                    animal.setDono(donoAnimal);
                    animais.add(animal);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro na listagem de animais: " + e.getMessage());
        }
        return animais;
    }

    @Override
    public List<Animal> BuscarPorDono(String CpfDono) {
        List<Animal> animaisDoDono = new ArrayList<>();
        String sql = "SELECT a.*, p.RFID, e.Nota_Fiscal, e.RFIDEX FROM Animal a LEFT JOIN Pet p ON a.ID = p.animal_ID LEFT JOIN Exotico e ON a.ID = e.animal_ID WHERE a.CPF_dono = ?";

                try (Connection conexao = conexaoBD.obterConexao();
                     PreparedStatement stmt = conexao.prepareStatement(sql)) {

                    stmt.setString(1, CpfDono);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Animal animal = null;
                            String rfidPet = rs.getString("RFID");
                            String notaFiscalExotico = rs.getString("Nota_Fiscal");

                            if(rfidPet != null) {
                                animal = new Pet();
                                ((Pet) animal).setrfid(rfidPet);
                            } else if (notaFiscalExotico != null) {
                                animal = new Exotico();
                                ((Exotico) animal).setNotaFiscal(notaFiscalExotico);
                                ((Exotico) animal).setRfidex(rs.getString("RFIDEX"));
                            }

                            if ( animal != null) {
                                animal.setId(rs.getInt("ID"));
                                animal.setNome(rs.getString("Nome"));
                                animal.setEspecie(rs.getString("Especie"));
                                animal.setIdade(rs.getInt("Idade"));
                                animal.setPorte(rs.getString("Porte"));

                                animaisDoDono.add(animal);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao buscar o animal pelo Dono: " + e.getMessage());
                }
                return animaisDoDono;
    }

    @Override
    public boolean deletarAnimal(int id) {
        String sqlSELECT = "SELECT p.animal_ID as petId, e.animal_ID as exoticoId FROM Animal a " +
                "LEFT JOIN Pet p ON a.ID = p.animal_ID " +
                "LEFT JOIN Exotico e ON a.ID = e.animal_ID " +
                "WHERE a.ID = ?";
        String sqlDeleteAnimal = "DELETE FROM Animal WHERE ID = ?";
        String sqlDeletePet = "DELETE FROM Pet WHERE animal_ID = ?";
        String sqlDeleteExotico = "DELETE FROM Exotico WHERE animal_ID = ?";

        try (Connection conexao = conexaoBD.obterConexao()) {
            try(PreparedStatement stmtSelect = conexao.prepareStatement(sqlSELECT)) {
                stmtSelect.setInt(1, id);
                try (ResultSet rs = stmtSelect.executeQuery()) {
                    if(rs.next()) {
                        if(rs.getObject("petID") != null) {
                            try (PreparedStatement stmtDeletPet = conexao.prepareStatement(sqlDeletePet)) {
                                stmtDeletPet.setInt(1, id);
                                stmtDeletPet.executeUpdate();
                            }
                        }  else if (rs.getObject("exoticoID") != null) {
                            try (PreparedStatement stmtDeleteExotico = conexao.prepareStatement(sqlDeleteExotico)) {
                                stmtDeleteExotico.setInt(1, id);
                                stmtDeleteExotico.executeUpdate();
                            }
                        }
                    }
                }
            }
            try (PreparedStatement stmtDeleAnimal = conexao.prepareStatement(sqlDeleteAnimal)){
                stmtDeleAnimal.setInt(1, id);
                int linhasAfetadas = stmtDeleAnimal.executeUpdate();
                return linhasAfetadas > 0;
            }
            } catch (Exception e) {
            System.err.println("Erro ao deletar o animal: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void atualizar(Animal animal) {
        String sqlAnimal = "UPDATE Animal SET Nome = ?,Especie = ?, Idade = ?, Porte = ? WHERE ID = ?";
        try (Connection conexao = conexaoBD.obterConexao()) {
            try(PreparedStatement stmtAnimal = conexao.prepareStatement(sqlAnimal)) {
                stmtAnimal.setString(1, animal.getNome());
                stmtAnimal.setString(2, animal.getEspecie());
                stmtAnimal.setInt(3, animal.getIdade());
                stmtAnimal.setString(4, animal.getPorte());
                stmtAnimal.setInt(5, animal.getId());

                stmtAnimal.executeUpdate();
            }

            if (animal instanceof Pet) {
                String sqlPet = "UPDATE Pet SET RFID = ? WHERE animal_ID = ?";

                try(PreparedStatement stmtPet = conexao.prepareStatement(sqlPet)) {
                    stmtPet.setString(1, ((Pet) animal).getrfid());
                    stmtPet.setInt(2, ((Pet) animal).getId());

                    stmtPet.executeUpdate();
                }
            } else if (animal instanceof Exotico) {
                String sqlExotico = "UPDATE Exotico SET RFIDEX = ? WHERE animal_ID = ?";

                try(PreparedStatement stmtExotico = conexao.prepareStatement(sqlExotico)) {
                    stmtExotico.setString(1, ((Exotico) animal).getNotaFiscal());
                    stmtExotico.setString(2, ((Exotico) animal).getRfidex());
                    stmtExotico.setInt(3, animal.getId());

                    stmtExotico.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o animal: " + e.getMessage());
        }
    }
}
