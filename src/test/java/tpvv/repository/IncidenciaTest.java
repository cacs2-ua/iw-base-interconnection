
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
public class IncidenciaTest {

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private EstadoIncidenciaRepository estadoIncidenciaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EstadoPagoRepository estadoPagoRepository;

    @Autowired
    private TarjetaPagoRepository tarjetaPagoRepository;

    private Incidencia crearYGuardarIncidencia(String titulo) {
        Pais pais = new Pais("default-country");
        paisRepository.save(pais);


        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais); // Asocia el país al comercio
        comercioRepository.save(comercio); // Guardar primero el comercio

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

        Incidencia incidencia = new Incidencia(titulo);
        incidencia.setUsuario_comercio(usuario);
        incidencia.setUsuario_tecnico(usuario2);
        incidencia.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia);

        EstadoPago estadoPago = new EstadoPago("default-state");
        estadoPagoRepository.save(estadoPago);

        TarjetaPago tarjetaPago = new TarjetaPago("default");
        tarjetaPagoRepository.save(tarjetaPago);

        Pago pago = new Pago("pago1");
        pago.setComercio(comercio);
        pago.setEstado(estadoPago);
        pago.setTarjetaPago(tarjetaPago);

        pagoRepository.save(pago);

        incidencia.setPago(pago);
        pago.setIncidencia(incidencia);

        pagoRepository.save(pago);
        incidenciaRepository.save(incidencia);

        return incidencia;
    }

    @Test
    public void crearIncidencia() {
        // GIVEN
        Incidencia incidencia = new Incidencia("Incidencia de prueba");

        // THEN
        assertThat(incidencia.getTitulo()).isEqualTo("Incidencia de prueba");
    }

    @Test
    public void comprobarIgualdadIncidenciasConId() {
        // GIVEN
        Incidencia incidencia1 = new Incidencia("Incidencia 1");
        Incidencia incidencia2 = new Incidencia("Incidencia 2");
        Incidencia incidencia3 = new Incidencia("Incidencia 3");

        incidencia1.setId(1L);
        incidencia2.setId(2L);
        incidencia3.setId(1L);

        // THEN
        assertThat(incidencia1).isEqualTo(incidencia3);
        assertThat(incidencia1).isNotEqualTo(incidencia2);
    }

    //
    // Tests IncidenciaRepository.
    //

    @Test
    @Transactional
    public void crearYBuscarIncidenciaBaseDatos() {
        // GIVEN
        Incidencia incidencia = crearYGuardarIncidencia("Incidencia de base de datos");

        // THEN
        Incidencia incidenciaBD = incidenciaRepository.findById(incidencia.getId()).orElse(null);
        assertThat(incidenciaBD).isNotNull();
        assertThat(incidenciaBD.getTitulo()).isEqualTo("Incidencia de base de datos");
        assertThat(incidenciaBD.getEstado().getNombre()).isEqualTo("default-state");
    }


    @Test
    @Transactional
    public void salvarIncidenciaEnBaseDatosConUsuarioNoBDLanzaExcepcion() {
        // GIVEN
        // Una incidencia nueva que no está en la BD y asociada a Usuarios no persistentes
        Incidencia incidencia = new Incidencia("Nueva incidencia");
        Usuario usuarioComercio = new Usuario("comercio-no-persistente@empresa.com");
        Usuario usuarioTecnico = new Usuario("tecnico-no-persistente@empresa.com");
        // No se guardan los usuarios en la BD

        incidencia.setUsuario_comercio(usuarioComercio);
        incidencia.setUsuario_tecnico(usuarioTecnico);

        EstadoIncidencia estadoIncidencia = new EstadoIncidencia("pendiente");
        estadoIncidenciaRepository.save(estadoIncidencia);
        incidencia.setEstado(estadoIncidencia);

        // WHEN // THEN
        // Se lanza una excepción al intentar salvar la incidencia en la BD debido a Usuarios no persistentes
        Assertions.assertThrows(Exception.class, () -> {
            incidenciaRepository.save(incidencia);
        });
    }

    //
    // Tests adicionales para verificar relaciones
    //

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // GIVEN
        Incidencia incidencia = crearYGuardarIncidencia("Incidencia para modificar");

        // WHEN
        Incidencia incidenciaBD = incidenciaRepository.findById(incidencia.getId()).orElse(null);
        incidenciaBD.setDescripcion("Descripción Modificada");
        incidenciaRepository.save(incidenciaBD);

        // Recuperar la Incidencia nuevamente para verificar el cambio
        Incidencia incidenciaModificada = incidenciaRepository.findById(incidencia.getId()).orElse(null);

        // THEN
        assertThat(incidenciaModificada).isNotNull();
        assertThat(incidenciaModificada.getDescripcion()).isEqualTo("Descripción Modificada");
    }

    @Test
    @Transactional
    public void salvarIncidenciaEnBaseDatosConEstadoIncidenciaNoBDLanzaExcepcion() {
        // GIVEN
        // Una incidencia nueva que no está en la BD y asociada a un EstadoIncidencia no persistente
        Incidencia incidencia = new Incidencia("Nueva incidencia");
        Usuario usuarioComercio = new Usuario("comercio-no-persistente@empresa.com");
        Usuario usuarioTecnico = new Usuario("tecnico-no-persistente@empresa.com");
        // No se guardan los usuarios en la BD

        incidencia.setUsuario_comercio(usuarioComercio);
        incidencia.setUsuario_tecnico(usuarioTecnico);

        EstadoIncidencia estadoIncidencia = new EstadoIncidencia("pendiente");
        estadoIncidenciaRepository.save(estadoIncidencia);
        incidencia.setEstado(estadoIncidencia);

        // WHEN // THEN
        // Se lanza una excepción al intentar salvar la incidencia en la BD debido a Usuarios no persistentes
        Assertions.assertThrows(Exception.class, () -> {
            incidenciaRepository.save(incidencia);
        });
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalConEstadoIncidenciaModificaLaBD() {
        // GIVEN
        Incidencia incidencia = crearYGuardarIncidencia("Incidencia para modificar");

        // Crear un nuevo EstadoIncidencia para asociarlo
        EstadoIncidencia nuevoEstado = new EstadoIncidencia("Nuevo Estado");
        estadoIncidenciaRepository.save(nuevoEstado);

        // WHEN
        Incidencia incidenciaBD = incidenciaRepository.findById(incidencia.getId()).orElse(null);
        incidenciaBD.setEstado(nuevoEstado); // Cambiar el estado asociado
        incidenciaRepository.save(incidenciaBD);

        // Recuperar la Incidencia nuevamente para verificar el cambio
        Incidencia incidenciaModificada = incidenciaRepository.findById(incidencia.getId()).orElse(null);

        // THEN
        assertThat(incidenciaModificada).isNotNull();
        assertThat(incidenciaModificada.getEstado().getNombre()).isEqualTo("Nuevo Estado");
    }

    /**
     * Test para crear una Incidencia con un Pago y verificar la relación uno a uno.
     */
    @Test
    @Transactional
    public void crearIncidenciaConPago() {
        // GIVEN
        Incidencia incidencia = crearYGuardarIncidencia("Incidencia con Pago");

        // Obtener el Pago asociado
        Pago pagoAsociado = incidencia.getPago();

        // THEN
        assertThat(incidencia.getId()).isNotNull();
        assertThat(pagoAsociado).isNotNull();
        assertThat(pagoAsociado.getIncidencia()).isEqualTo(incidencia);

        // Verificar desde el lado de Pago
        Pago pagoBD = pagoRepository.findById(pagoAsociado.getId()).orElse(null);
        assertThat(pagoBD).isNotNull();
        assertThat(pagoBD.getIncidencia()).isEqualTo(incidencia);
    }

    /**
     * Test para actualizar la relación entre Incidencia y Pago.
     */
    @Test
    @Transactional
    public void actualizarRelacionIncidenciaPago() {
        // GIVEN
        Incidencia incidencia = crearYGuardarIncidencia("Incidencia para actualizar Pago");
        Pago nuevoPago = new Pago("pago-nuevo");
        nuevoPago.setComercio(incidencia.getUsuario_comercio().getComercio());
        nuevoPago.setEstado(estadoPagoRepository.save(new EstadoPago("estado-nuevo")));
        nuevoPago.setTarjetaPago(tarjetaPagoRepository.save(new TarjetaPago("tarjeta-nueva")));
        pagoRepository.save(nuevoPago);

        // WHEN
        // Actualizar la Incidencia para que apunte al nuevo Pago
        incidencia.setPago(nuevoPago);
        incidenciaRepository.save(incidencia);

        // THEN
        Incidencia incidenciaBD = incidenciaRepository.findById(incidencia.getId()).orElse(null);
        Pago pagoAntiguoBD = pagoRepository.findById(pagoRepository.findAll().stream()
                .filter(p -> p.getTicketExt().equals("pago1"))
                .findFirst()
                .get().getId()).orElse(null);
        Pago pagoNuevoBD = pagoRepository.findById(nuevoPago.getId()).orElse(null);

        assertThat(incidenciaBD).isNotNull();
        assertThat(incidenciaBD.getPago()).isEqualTo(pagoNuevoBD);
        assertThat(pagoNuevoBD.getIncidencia()).isEqualTo(incidenciaBD);
        assertThat(pagoAntiguoBD.getIncidencia()).isNull();
    }

    /**
     * Test para desasociar un Pago de una Incidencia.
     */
    @Test
    @Transactional
    public void desasociarPagoDeIncidencia() {
        // GIVEN
        Incidencia incidencia = crearYGuardarIncidencia("Incidencia para desasociar Pago");

        // WHEN
        // Desasociar el Pago de la Incidencia
        Pago pago = incidencia.getPago();
        incidencia.setPago(null);
        incidenciaRepository.save(incidencia);

        // THEN
        Incidencia incidenciaBD = incidenciaRepository.findById(incidencia.getId()).orElse(null);
        Pago pagoBD = pagoRepository.findById(pago.getId()).orElse(null);

        assertThat(incidenciaBD).isNotNull();
        assertThat(incidenciaBD.getPago()).isNull();
        assertThat(pagoBD).isNotNull();
        assertThat(pagoBD.getIncidencia()).isNull();
    }


    /**
     * Test para verificar que al guardar una Incidencia con un Pago ya persistido, la relación se establece correctamente.
     */
    @Test
    @Transactional
    public void guardarIncidenciaConPagoYaPersistido() {
        // GIVEN
        Incidencia incidencia = crearYGuardarIncidencia("Incidencia con Pago ya persistido");
        Pago pagoExistente = pagoRepository.findByTicketExt("pago1").orElse(null);
        assertThat(pagoExistente).isNotNull();

        // WHEN
        // Asociar nuevamente el mismo Pago
        incidencia.setPago(pagoExistente);
        incidenciaRepository.save(incidencia);

        // THEN
        Incidencia incidenciaBD = incidenciaRepository.findById(incidencia.getId()).orElse(null);
        Pago pagoBD = pagoRepository.findById(pagoExistente.getId()).orElse(null);

        assertThat(incidenciaBD).isNotNull();
        assertThat(incidenciaBD.getPago()).isEqualTo(pagoBD);
        assertThat(pagoBD.getIncidencia()).isEqualTo(incidenciaBD);
    }



}
