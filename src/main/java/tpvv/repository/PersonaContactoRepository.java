package tpvv.repository;

import tpvv.model.PersonaContacto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonaContactoRepository extends JpaRepository<PersonaContacto, Long> {
    Optional<PersonaContacto> findByEmail(String emailExt);

}