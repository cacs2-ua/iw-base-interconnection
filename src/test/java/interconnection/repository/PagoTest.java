// test/repository/UsuarioTest.java

package interconnection.repository;

import interconnection.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "/sql/clean-test-db.sql")
public class PagoTest {

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
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private EstadoIncidenciaRepository estadoIncidenciaRepository;


    @Autowired
    private EstadoPagoRepository estadoPagoRepository;

    @Autowired
    private TarjetaPagoRepository tarjetaPagoRepository;

    private Pago crearYGuardarPago(String ticket) {
        Pais pais = new Pais("default-country");
        paisRepository.save(pais);

        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

        TipoUsuario tipoUsuario = new TipoUsuario("default-type");
        tipoUsuarioRepository.save(tipoUsuario);

        Usuario usuario = new Usuario("default-email");
        usuario.setTipo(tipoUsuario);
        usuario.setComercio(comercio);
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario("default-email2");
        usuario2.setTipo(tipoUsuario);
        usuario2.setComercio(comercio);
        usuarioRepository.save(usuario2);

        comercio.getUsuarios().add(usuario);

        comercioRepository.save(comercio);

        EstadoIncidencia estadoIncidencia = new EstadoIncidencia("default-state");
        estadoIncidenciaRepository.save(estadoIncidencia);

        Incidencia incidencia = new Incidencia("default-title");
        incidencia.setUsuario_comercio(usuario);
        incidencia.setUsuario_tecnico(usuario2);
        incidencia.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia);

        EstadoPago estadoPago = new EstadoPago("default-state");
        estadoPagoRepository.save(estadoPago);

        TarjetaPago tarjetaPago = new TarjetaPago("default");
        tarjetaPagoRepository.save(tarjetaPago);

        Pago pago = new Pago(ticket);
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

        incidencia.setPago(pago);
        pago.setIncidencia(incidencia);

        incidenciaRepository.save(incidencia);
        pagoRepository.save(pago);
        return pago;
    }

    @Test
    public void crearPago() {
        // GIVEN
        Pago pago = new Pago("ticket@pago.com");

        // THEN
        assertThat(pago.getTicketExt()).isEqualTo("ticket@pago.com");
    }


    @Test
    public void comprobarIgualdadPagosConId() {
        // GIVEN
        Pago pago1 = new Pago("ticket1@pago.com");
        Pago pago2 = new Pago("ticket2@pago.com");
        Pago pago3 = new Pago("ticket3@pago.com");

        pago1.setId(1L);
        pago2.setId(2L);
        pago3.setId(1L);

        // THEN
        assertThat(pago1).isEqualTo(pago3);
        assertThat(pago1).isNotEqualTo(pago2);
    }

    //
    // Tests PagoRepository.
    //

    @Test
    @Transactional
    public void crearYBuscarPagoBaseDatos() {
        // GIVEN
        Pago pago = crearYGuardarPago("ticket@pago.com");

        // THEN
        Pago pagoBD = pagoRepository.findById(pago.getId()).orElse(null);
        assertThat(pagoBD).isNotNull();
        assertThat(pagoBD.getTicketExt()).isEqualTo("ticket@pago.com");
        assertThat(pagoBD.getComercio().getCif()).isEqualTo("default-cif");
    }

    @Test
    @Transactional
    public void buscarPagoPorTicket() {
        // GIVEN
        crearYGuardarPago("ticket@pago.com");

        // WHEN
        Pago pagoBD = pagoRepository.findByTicketExt("ticket@pago.com").orElse(null);

        // THEN
        assertThat(pagoBD.getTicketExt()).isEqualTo("ticket@pago.com"); // Asumiendo un valor por defecto
    }

    @Test
    @Transactional
    public void salvarPagoEnBaseDatosConComercioNoBDLanzaExcepcion() {
        // GIVEN
        // Un pago nuevo que no está en la BD y asociado a un comercio no persistente
        Pago pago = new Pago("nuevo-ticket");
        Comercio comercio = new Comercio("comercio-nuevo");

        pago.setComercio(comercio);

        // WHEN // THEN
        // se lanza una excepción al intentar salvar el pago en la BD
        Assertions.assertThrows(Exception.class, () -> {
            pagoRepository.save(pago);
        });
    }


    @Test
    @Transactional
    public void salvarTarjetaPagoEnBaseDatosConTarjetaPagoNoBDLanzaExcepcion() {
        // GIVEN
        // Un pago nuevo que no está en la BD y asociado a una TarjetaPago no persistente
        Pago pago = new Pago("nuevo-ticket");
        TarjetaPago tarjetaPago = new TarjetaPago("tarjeta-no-persistente");
        // No se guarda tarjetaPago en la BD

        pago.setTarjetaPago(tarjetaPago);

        // Crear y guardar comercio para evitar violaciones de clave foránea
        Pais pais = new Pais("pais-test");
        paisRepository.save(pais);

        Comercio comercio = new Comercio("cif-test");
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

        pago.setComercio(comercio);

        // Crear y guardar EstadoPago para evitar violaciones de clave foránea
        EstadoPago estadoPago = new EstadoPago("estado-test");
        estadoPagoRepository.save(estadoPago);
        pago.setEstado(estadoPago);

        // WHEN // THEN
        // Se lanza una excepción al intentar salvar el pago en la BD debido a TarjetaPago no persistente
        Assertions.assertThrows(Exception.class, () -> {
            pagoRepository.save(pago);
        });
    }



}
