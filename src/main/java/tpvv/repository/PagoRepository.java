package tpvv.repository;

import tpvv.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByTicketExt(String ticket);
}
