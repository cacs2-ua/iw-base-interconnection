import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SqlExecutor {

    private static final String DB_URL = "jdbc:postgresql://localhost:5562/tpvv_int";
    private static final String DB_USER = "tpvv_int";
    private static final String DB_PASSWD = "tpvv_int";
    private static final String SQL_FILE_PATH = "src/main/resources/clean-develop-db.sql"; // Cambia esto según tu ruta

    public static void main(String[] args) {
        System.out.println("Ejecutando el archivo SQL: " + SQL_FILE_PATH);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD)) {
            System.out.println("Conexión establecida con la base de datos.");

            // Leer el archivo SQL
            BufferedReader reader = new BufferedReader(new FileReader(SQL_FILE_PATH));
            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }
            reader.close();

            // Divide el archivo SQL en sentencias separadas por ';'
            String[] sqlStatements = sqlBuilder.toString().split(";");

            // Ejecutar cada sentencia SQL
            try (Statement statement = connection.createStatement()) {
                for (String sql : sqlStatements) {
                    String trimmedSql = sql.trim();
                    if (!trimmedSql.isEmpty()) {
                        statement.execute(trimmedSql);
                        System.out.println("Ejecutada: " + trimmedSql);
                    }
                }
                System.out.println("Todas las sentencias SQL se ejecutaron correctamente.");
            }

        } catch (Exception e) {
            System.err.println("Error ejecutando el archivo SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
