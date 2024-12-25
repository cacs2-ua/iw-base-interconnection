package tpvv.seeder;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tpvv.model.Pais;
import tpvv.repository.PaisRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class PaisSeeder {

    @Autowired
    private PaisRepository paisRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void seed() {
        // Ejecutar el script clean-db.sql para limpiar la base de datos
        executeSqlScript("clean-db.sql");

        // Reiniciar la secuencia de IDs
        resetSequence();

        // Crear lista de 10 países
        List<Pais> paises = Arrays.asList(
                new Pais("España"),
                new Pais("Francia"),
                new Pais("Alemania"),
                new Pais("Italia"),
                new Pais("Portugal"),
                new Pais("Reino Unido"),
                new Pais("Países Bajos"),
                new Pais("Bélgica"),
                new Pais("Suiza"),
                new Pais("Suecia")
        );

        // Guardar los nuevos países en la base de datos
        paisRepository.saveAll(paises);

        System.out.println("Seeder Pais ejecutado correctamente.");
    }

    private void resetSequence() {
        try {
            // Consulta para obtener el nombre de la secuencia asociada a la tabla 'paises'
            String sequenceNameQuery = "SELECT pg_get_serial_sequence('paises', 'id')";
            Query sequenceQuery = entityManager.createNativeQuery(sequenceNameQuery);
            String sequenceName = (String) sequenceQuery.getSingleResult();

            // Reinicia la secuencia para empezar desde 1
            String resetQuery = "ALTER SEQUENCE " + sequenceName + " RESTART WITH 1";
            entityManager.createNativeQuery(resetQuery).executeUpdate();

            System.out.println("Secuencia " + sequenceName + " reiniciada a 1.");
        } catch (Exception e) {
            System.err.println("Error al reiniciar la secuencia: " + e.getMessage());
        }
    }

    private void executeSqlScript(String scriptPath) {
        try {
            // Cargar el archivo desde la carpeta resources
            ClassPathResource resource = new ClassPathResource(scriptPath);

            // Leer el contenido del archivo línea por línea
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder sqlBuilder = new StringBuilder();
                String line;

                // Concatenar líneas y ejecutar cada comando SQL
                while ((line = reader.readLine()) != null) {
                    // Ignorar líneas vacías o comentarios
                    if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                        continue;
                    }
                    sqlBuilder.append(line).append(" ");
                    if (line.trim().endsWith(";")) {
                        // Ejecutar el comando SQL
                        String sql = sqlBuilder.toString();
                        entityManager.createNativeQuery(sql).executeUpdate();
                        sqlBuilder.setLength(0); // Resetear para el próximo comando
                    }
                }
            }

            System.out.println("Script SQL ejecutado correctamente: " + scriptPath);
        } catch (Exception e) {
            System.err.println("Error al ejecutar el script SQL: " + e.getMessage());
        }
    }
}
