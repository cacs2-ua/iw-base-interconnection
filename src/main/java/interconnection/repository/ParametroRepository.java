package interconnection.repository;

import interconnection.model.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParametroRepository extends JpaRepository<Parametro, Long> {
    Optional<Parametro> findByNombre(String nombre);
}
