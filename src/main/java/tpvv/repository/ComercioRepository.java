// repository/ComercioRepository.java

package tpvv.repository;

import tpvv.model.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ComercioRepository extends JpaRepository<Comercio, Long> {
    Optional<Comercio> findByCif(String cif);
    Optional<Comercio> findByApiKey(String apiKey);

    boolean existsByCif(String s);
}
