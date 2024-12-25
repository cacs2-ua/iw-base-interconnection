// test/repository/ComercioTest.java

package tpvv.repository;

import tpvv.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class PersonaContactoTest {

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EstadoPagoRepository estadoPagoRepository;

    @Autowired
    private TarjetaPagoRepository tarjetaPagoRepository;

    @Autowired
    private PersonaContactoRepository personaContactoRepository;

    // Métodos auxiliares para reducir duplicación

    private PersonaContacto crearYGuardarPersonaContacto(String email) {
        Pais pais = new Pais("default-country");
        paisRepository.save(pais);

        Comercio comercio = new Comercio("CIF123456");
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

        TipoUsuario tipoUsuario = new TipoUsuario("default-type");
        tipoUsuarioRepository.save(tipoUsuario);

        Usuario usuario = new Usuario("default@gmail.com");
        usuario.setTipo(tipoUsuario);
        usuario.setComercio(comercio);
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario("default2@gmail.com");
        usuario2.setTipo(tipoUsuario);
        usuario2.setComercio(comercio);
        usuarioRepository.save(usuario2);

        Usuario usuario3 = new Usuario("default3@gmail.com");
        usuario3.setTipo(tipoUsuario);
        usuario3.setComercio(comercio);
        usuarioRepository.save(usuario3);

        comercio.getUsuarios().add(usuario);
        comercio.getUsuarios().add(usuario2);
        comercio.getUsuarios().add(usuario3);

        EstadoPago estadoPago = new EstadoPago("default-state");
        estadoPagoRepository.save(estadoPago);

        TarjetaPago tarjetaPago = new TarjetaPago("default");
        tarjetaPagoRepository.save(tarjetaPago);

        Pago pago = new Pago("pago1");
        pago.setComercio(comercio);
        pago.setEstado(estadoPago);
        pago.setTarjetaPago(tarjetaPago);

        Pago pago2 = new Pago("pago2");
        pago2.setComercio(comercio);
        pago2.setEstado(estadoPago);
        pago2.setTarjetaPago(tarjetaPago);

        Pago pago3 = new Pago("pago3");
        pago3.setComercio(comercio);
        pago3.setEstado(estadoPago);
        pago3.setTarjetaPago(tarjetaPago);

        pagoRepository.save(pago);
        pagoRepository.save(pago2);
        pagoRepository.save(pago3);

        comercio.addPago(pago);
        comercio.addPago(pago2);
        comercio.addPago(pago3);

        comercioRepository.save(comercio);

        PersonaContacto personaContacto = new PersonaContacto(email);
        personaContacto.setComercio(comercio);
        personaContactoRepository.save(personaContacto);
        comercio.setPersonaContacto(personaContacto);
        comercioRepository.save(comercio);

        return personaContacto;
    }

    @Test
    @Transactional
    public void crearYGuardarPersonaContactoSinComercio() {
        // GIVEN
        PersonaContacto personaContacto = new PersonaContacto("contacto@sincomercio.com");
        personaContactoRepository.save(personaContacto);

        // THEN
        PersonaContacto personaContactoBD = personaContactoRepository.findById(personaContacto.getId()).orElse(null);
        assertThat(personaContactoBD).isNotNull();
        assertNull(personaContactoBD.getComercio());
    }



}
