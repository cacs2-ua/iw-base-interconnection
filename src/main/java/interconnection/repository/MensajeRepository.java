// repository/ComercioRepository.java

package interconnection.repository;

import interconnection.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

}
