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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class TarjetaPagoTest {

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

    private TarjetaPago crearYGuardarTarjetaPago(String numeroTarjeta) {

        Pais pais = new Pais("default-country");
        paisRepository.save(pais);

        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

        EstadoPago estadoPago = new EstadoPago("default-estado");
        estadoPagoRepository.save(estadoPago);

        TarjetaPago tarjetaPago = new TarjetaPago(numeroTarjeta);
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

        tarjetaPago.addPago(pago);
        tarjetaPago.addPago(pago2);
        tarjetaPago.addPago(pago3);

        estadoPagoRepository.save(estadoPago);
        tarjetaPagoRepository.save(tarjetaPago);

        return tarjetaPago;
    }

    @Test
    public void crearTarjetaPago() {
        // GIVEN
        TarjetaPago tarjetaPago = new TarjetaPago("1234-5678-9012-3456");

        // THEN
        assertThat(tarjetaPago.getNumeroTarjeta()).isEqualTo("1234-5678-9012-3456");
    }

    @Test
    public void comprobarIgualdadTarjetaPagosSinId() {
        // GIVEN
        TarjetaPago tarjetaPago1 = new TarjetaPago("1234-5678-9012-3456");
        TarjetaPago tarjetaPago2 = new TarjetaPago("1234-5678-9012-3456");
        TarjetaPago tarjetaPago3 = new TarjetaPago("6543-2109-8765-4321");

        // THEN
        assertThat(tarjetaPago1).isEqualTo(tarjetaPago2);
        assertThat(tarjetaPago1).isNotEqualTo(tarjetaPago3);
    }

    @Test
    public void comprobarIgualdadTarjetaPagosConId() {
        // GIVEN
        TarjetaPago tarjetaPago1 = new TarjetaPago("1111-2222-3333-4444");
        TarjetaPago tarjetaPago2 = new TarjetaPago("5555-6666-7777-8888");
        TarjetaPago tarjetaPago3 = new TarjetaPago("1111-2222-3333-4444");

        tarjetaPago1.setId(1L);
        tarjetaPago2.setId(2L);
        tarjetaPago3.setId(1L);

        // THEN
        assertThat(tarjetaPago1).isEqualTo(tarjetaPago3);
        assertThat(tarjetaPago1).isNotEqualTo(tarjetaPago2);
    }

    //
    // Tests TarjetaPagoRepository.
    //

    @Test
    @Transactional
    public void crearYBuscarTarjetaPagoBaseDatos() {
        // GIVEN
        TarjetaPago tarjetaPago = crearYGuardarTarjetaPago("1234-5678-9012-3456");

        // THEN
        TarjetaPago tarjetaPagoBD = tarjetaPagoRepository.findById(tarjetaPago.getId()).orElse(null);
        assertThat(tarjetaPagoBD).isNotNull();
        assertThat(tarjetaPagoBD.getNumeroTarjeta()).isEqualTo("1234-5678-9012-3456");
    }

    @Test
    @Transactional
    public void buscarTarjetaPagoPorNumero() {
        // GIVEN
        crearYGuardarTarjetaPago("1234-5678-9012-3456");

        // WHEN
        TarjetaPago tarjetaPagoBD = tarjetaPagoRepository.findByNumeroTarjeta("1234-5678-9012-3456").orElse(null);

        // THEN
        assertThat(tarjetaPagoBD).isNotNull();
        assertThat(tarjetaPagoBD.getNumeroTarjeta()).isEqualTo("1234-5678-9012-3456");
    }

    @Test
    @Transactional
    public void salvarTarjetaPagoEnBaseDatosConPagosAsociados() {
        // GIVEN
        TarjetaPago tarjetaPago = crearYGuardarTarjetaPago("9999-8888-7777-6666");

        // WHEN
        TarjetaPago tarjetaPagoBD = tarjetaPagoRepository.findById(tarjetaPago.getId()).orElse(null);

        // THEN
        assertThat(tarjetaPagoBD).isNotNull();
        assertThat(tarjetaPagoBD.getPagos()).hasSize(3);
    }

    @Test
    @Transactional
    public void unTarjetaPagoTieneUnaListaDePagos() {
        // GIVEN
        TarjetaPago tarjetaPago = crearYGuardarTarjetaPago("5555-4444-3333-2222");

        // WHEN
        TarjetaPago tarjetaPagoRecuperado = tarjetaPagoRepository.findById(tarjetaPago.getId()).orElse(null);

        // THEN
        assertThat(tarjetaPagoRecuperado).isNotNull();
        assertThat(tarjetaPagoRecuperado.getPagos()).hasSize(3);
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // GIVEN
        TarjetaPago tarjetaPago = crearYGuardarTarjetaPago("1111-2222-3333-4444");

        // WHEN
        TarjetaPago tarjetaPagoBD = tarjetaPagoRepository.findById(tarjetaPago.getId()).orElse(null);
        tarjetaPagoBD.setNumeroTarjeta("5555-6666-7777-8888");
        tarjetaPagoRepository.save(tarjetaPagoBD);

        // Recuperar la TarjetaPago nuevamente para verificar el cambio
        TarjetaPago tarjetaPagoModificado = tarjetaPagoRepository.findById(tarjetaPago.getId()).orElse(null);

        // THEN
        assertThat(tarjetaPagoModificado).isNotNull();
        assertThat(tarjetaPagoModificado.getNumeroTarjeta()).isEqualTo("5555-6666-7777-8888");
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
