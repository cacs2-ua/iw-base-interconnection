package tpvv.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private PaisSeeder paisSeeder;

    @Override
    public void run(String... args) throws Exception {
        // Imprime los argumentos para diagnóstico
        System.out.println(">>> ARGS: " + Arrays.toString(args));

        // Comprueba si el argumento "seed" está presente
        boolean hasSeedArg = Arrays.stream(args)
                .anyMatch(a -> a.equalsIgnoreCase("seed"));

        if (hasSeedArg) {
            paisSeeder.seed();
            System.out.println("Seeder general ejecutado correctamente.");
        }
    }
}
