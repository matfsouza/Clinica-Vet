package org.example.ucb.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaBancoTest {

    @Test
    @DisplayName("schema.sql cria as 9 tabelas usadas pelos DAOs")
    void schemaCriaAsNoveTabelas() throws Exception {
        BancoDeTesteH2 banco = new BancoDeTesteH2();
        List<String> tabelas = new ArrayList<>();
        try (Connection c = banco.obterConexao();
             ResultSet rs = c.getMetaData().getTables(null, "public", "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tabelas.add(rs.getString("TABLE_NAME").toLowerCase());
            }
        }
        tabelas.sort(String::compareTo);
        assertEquals(List.of("animal", "certificacao", "consulta", "dono", "especialidade",
                "exotico", "pet", "tratamento", "veterinario"), tabelas);
    }

    @Test
    @DisplayName("chave estrangeira impede animal com dono inexistente")
    void chaveEstrangeiraBloqueiaAnimalSemDono() {
        BancoDeTesteH2 banco = new BancoDeTesteH2();
        assertThrows(IllegalStateException.class, () -> banco.executar(
                "INSERT INTO Animal (Nome, Especie, Idade, Porte, CPF_Dono) VALUES ('Rex', 'Cão', 3, 'Médio', '000')"));
    }
}
