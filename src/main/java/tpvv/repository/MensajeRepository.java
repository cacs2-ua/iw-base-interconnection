// repository/ComercioRepository.java

package tpvv.repository;

import tpvv.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

}
