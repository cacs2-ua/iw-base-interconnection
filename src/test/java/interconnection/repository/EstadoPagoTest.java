// test/repository/ComercioTest.java

package interconnection.repository;

import interconnection.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EstadoPagoTest {

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

    // Métodos auxiliares para reducir duplicación

    private EstadoPago crearYGuardarEstadoPago(String state) {

        Pais pais = new Pais("default-country");
        paisRepository.save(pais);

        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

        EstadoPago estadoPago = new EstadoPago(state);
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

        estadoPago.addPago(pago);
        estadoPago.addPago(pago2);
        estadoPago.addPago(pago3);

        estadoPagoRepository.save(estadoPago);

        return estadoPago;
    }

    @Test
    public void crearEstadoPago() {
        // GIVEN
        EstadoPago estadoPago = new EstadoPago("pendiente");

        // THEN
        assertThat(estadoPago.getNombre()).isEqualTo("pendiente");
    }

    @Test
    public void comprobarIgualdadEstadoPagosConId() {
        // GIVEN
        EstadoPago estadoPago1 = new EstadoPago("pendiente");
        EstadoPago estadoPago2 = new EstadoPago("completado");
        EstadoPago estadoPago3 = new EstadoPago("pendiente");

        estadoPago1.setId(1L);
        estadoPago2.setId(2L);
        estadoPago3.setId(1L);

        // THEN
        assertThat(estadoPago1).isEqualTo(estadoPago3);
        assertThat(estadoPago1).isNotEqualTo(estadoPago2);
    }

    //
    // Tests EstadoPagoRepository.
    //

    @Test
    @Transactional
    public void crearYBuscarEstadoPagoBaseDatos() {
        // GIVEN
        EstadoPago estadoPago = crearYGuardarEstadoPago("pendiente");

        // THEN
        EstadoPago estadoPagoBD = estadoPagoRepository.findById(estadoPago.getId()).orElse(null);
        assertThat(estadoPagoBD).isNotNull();
        assertThat(estadoPagoBD.getNombre()).isEqualTo("pendiente");
    }

    @Test
    @Transactional
    public void salvarEstadoPagoEnBaseDatosSinPagos() {
        // GIVEN
        EstadoPago estadoPago = new EstadoPago("nuevo-estado");
        // No se asocian pagos

        // WHEN
        EstadoPago estadoPagoGuardado = estadoPagoRepository.save(estadoPago);

        // THEN
        assertThat(estadoPagoGuardado.getId()).isNotNull();
        assertThat(estadoPagoGuardado.getNombre()).isEqualTo("nuevo-estado");
        assertThat(estadoPagoGuardado.getPagos()).isEmpty();
    }

    @Test
    @Transactional
    public void unEstadoPagoTieneUnaListaDePagos() {
        // GIVEN
        EstadoPago estadoPago = crearYGuardarEstadoPago("estado-multiple-pagos");

        // WHEN
        EstadoPago estadoPagoRecuperado = estadoPagoRepository.findById(estadoPago.getId()).orElse(null);

        // THEN
        assertThat(estadoPagoRecuperado).isNotNull();
        assertThat(estadoPagoRecuperado.getPagos()).hasSize(3);
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalConPagoModificaLaBD() {
        // GIVEN
        EstadoPago estadoPago = crearYGuardarEstadoPago("estado-original");

        // Recuperar un Pago asociado al EstadoPago
        Pago pago = estadoPago.getPagos().iterator().next(); // Tomar el primer pago de la lista
        Long pagoId = pago.getId();

        // WHEN
        Pago pagoBD = pagoRepository.findById(pagoId).orElse(null);
        assertThat(pagoBD).isNotNull();

        // Modificar un atributo del Pago
        pagoBD.setTicketExt("Ticket Modificado");
        pagoRepository.save(pagoBD);

        // Recuperar el Pago nuevamente para verificar el cambio
        Pago pagoModificado = pagoRepository.findById(pagoId).orElse(null);

        // THEN
        assertThat(pagoModificado).isNotNull();
        assertThat(pagoModificado.getTicketExt()).isEqualTo("Ticket Modificado");
    }




}
