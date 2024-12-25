package interconnection.repository;

import interconnection.model.TarjetaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TarjetaPagoRepository extends JpaRepository<TarjetaPago, Long> {
    Optional<TarjetaPago> findByNumeroTarjeta(String numeroTarjeta);
}
