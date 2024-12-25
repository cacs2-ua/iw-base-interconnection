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

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class ComercioTest {

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

    private Comercio crearYGuardarComercio(String cif) {
        Pais pais = new Pais("default-country");
        paisRepository.save(pais);

        Comercio comercio = new Comercio(cif);
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

        return comercio;
    }

    private Comercio crearYGuardarComercio2(String cif) {
        Pais pais = new Pais("default-countryx2");
        paisRepository.save(pais);

        Comercio comercio = new Comercio(cif);
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

        TipoUsuario tipoUsuario = new TipoUsuario("default-typex2");
        tipoUsuarioRepository.save(tipoUsuario);

        Usuario usuario = new Usuario("default@gmail.comx2");
        usuario.setTipo(tipoUsuario);
        usuario.setComercio(comercio);
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario("default2@gmail.comx2");
        usuario2.setTipo(tipoUsuario);
        usuario2.setComercio(comercio);
        usuarioRepository.save(usuario2);

        Usuario usuario3 = new Usuario("default3@gmail.comx2");
        usuario3.setTipo(tipoUsuario);
        usuario3.setComercio(comercio);
        usuarioRepository.save(usuario3);

        comercio.getUsuarios().add(usuario);
        comercio.getUsuarios().add(usuario2);
        comercio.getUsuarios().add(usuario3);

        EstadoPago estadoPago = new EstadoPago("default-statex2");
        estadoPagoRepository.save(estadoPago);

        TarjetaPago tarjetaPago = new TarjetaPago("defaultx2");
        tarjetaPagoRepository.save(tarjetaPago);

        Pago pago = new Pago("pago1x2");
        pago.setComercio(comercio);
        pago.setEstado(estadoPago);
        pago.setTarjetaPago(tarjetaPago);

        Pago pago2 = new Pago("pago2x2");
        pago2.setComercio(comercio);
        pago2.setEstado(estadoPago);
        pago2.setTarjetaPago(tarjetaPago);

        Pago pago3 = new Pago("pago3x2");
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

        return comercio;
    }


    //
    // Tests modelo Comercio en memoria, sin la conexión con la BD
    //

    @Test
    public void crearComercio() {
        // GIVEN
        Comercio comercio = new Comercio("CIF123456");

        // THEN
        assertThat(comercio.getNombre()).isEqualTo("default-name");
        assertThat(comercio.getCif()).isEqualTo("CIF123456");
        assertThat(comercio.getPais()).isEqualTo("default-country");
        assertThat(comercio.getProvincia()).isEqualTo("default-province");
        assertThat(comercio.getDireccion()).isEqualTo("default-address");
        assertThat(comercio.getIban()).isEqualTo("default-iban");
        assertThat(comercio.getApiKey()).isEqualTo("default-apiKey");
        assertThat(comercio.getUrl_back()).isEqualTo("default-url_back");
    }

    @Test
    public void comprobarIgualdadComerciosSinId() {
        // GIVEN
        Comercio comercio1 = new Comercio("CIF123456");
        Comercio comercio2 = new Comercio("CIF123456");
        Comercio comercio3 = new Comercio("CIF654321");

        // THEN
        assertThat(comercio1).isEqualTo(comercio2);
        assertThat(comercio1).isNotEqualTo(comercio3);
    }

    @Test
    public void comprobarIgualdadComerciosConId() {
        // GIVEN
        Comercio comercio1 = new Comercio("CIF123456");
        Comercio comercio2 = new Comercio("CIF654321");
        Comercio comercio3 = new Comercio("CIF123433");

        comercio1.setId(1L);
        comercio2.setId(2L);
        comercio3.setId(1L);

        // THEN
        assertThat(comercio1).isEqualTo(comercio3);
        assertThat(comercio1).isNotEqualTo(comercio2);
    }

    //
    // Tests ComercioRepository.
    //

    @Test
    @Transactional
    public void crearYBuscarComercioBaseDatos() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF123456");

        // THEN
        assertThat(comercio.getId()).isNotNull();

        Comercio comercioBD = comercioRepository.findById(comercio.getId()).orElse(null);
        assertThat(comercioBD.getNombre()).isEqualTo("default-name");
        assertThat(comercioBD.getCif()).isEqualTo("CIF123456");
    }

    @Test
    @Transactional
    public void buscarComercioPorCif() {
        // GIVEN
        crearYGuardarComercio("CIF123456");

        // WHEN
        Comercio comercioBD = comercioRepository.findByCif("CIF123456").orElse(null);

        // THEN
        assertThat(comercioBD.getNombre()).isEqualTo("default-name");
    }

    @Test
    @Transactional
    public void unComercioTieneUnaListaDeUsuarios() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF123456");

        // WHEN
        Comercio comercioRecuperado = comercioRepository.findById(comercio.getId()).orElse(null);

        // THEN
        assertThat(comercioRecuperado.getUsuarios()).hasSize(3);
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF123456");

        // WHEN
        Comercio comercioBD = comercioRepository.findById(comercio.getId()).orElse(null);
        Usuario usuarioDB = comercioBD.getUsuarios().iterator().next();
        usuarioDB.setNombre("Usuario Uno Modificado");

        usuarioDB = usuarioRepository.findById(usuarioDB.getId()).orElse(null);

        // THEN
        assertThat(usuarioDB.getNombre()).isEqualTo("Usuario Uno Modificado");
    }

    @Test
    @Transactional
    public void unComercioTieneUnaListaDePagos() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF123456");

        // WHEN
        Comercio comercioRecuperado = comercioRepository.findById(comercio.getId()).orElse(null);

        // THEN
        assertThat(comercioRecuperado.getPagos()).hasSize(3);
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalConPagoModificaLaBD() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF123456");

        // WHEN
        Comercio comercioBD = comercioRepository.findById(comercio.getId()).orElse(null);
        Pago pagoDB = comercioBD.getPagos().iterator().next();
        pagoDB.setTicketExt("Pago Modificado");

        pagoDB = pagoRepository.findById(pagoDB.getId()).orElse(null);

        // THEN
        assertThat(pagoDB.getTicketExt()).isEqualTo("Pago Modificado");
    }

    @Test
    @Transactional
    public void salvarComercioEnBaseDatosConPaisNoBDLanzaExcepcion() {
        // GIVEN

        Comercio comercio = new Comercio("CIF123456");
        Pais pais = new Pais("default-country");

        // WHEN // THEN

        Assertions.assertThrows(Exception.class, () -> {
            comercioRepository.save(comercio);
        });
    }

    /**
     * Test para crear y guardar una PersonaContacto sin asociarla a un Comercio.
     */
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

    /**
     * Test para crear y guardar una PersonaContacto con un Comercio.
     */
    @Test
    @Transactional
    public void crearYGuardarPersonaContactoConComercio() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF007");
        PersonaContacto personaContacto = new PersonaContacto("contacto@comercio.com");
        personaContacto.setComercio(comercio);
        personaContactoRepository.save(personaContacto);
        comercio.setPersonaContacto(personaContacto);
        comercioRepository.save(comercio);

        // THEN
        PersonaContacto personaContactoBD = personaContactoRepository.findById(personaContacto.getId()).orElse(null);
        Comercio comercioBD = comercioRepository.findById(comercio.getId()).orElse(null);

        assertThat(personaContactoBD).isNotNull();
        assertThat(personaContactoBD.getEmail()).isEqualTo("contacto@comercio.com");
        assertThat(personaContactoBD.getComercio()).isEqualTo(comercioBD);

        assertThat(comercioBD).isNotNull();
        assertThat(comercioBD.getPersonaContacto()).isEqualTo(personaContactoBD);
    }

    /**
     * Test para actualizar el Comercio asociado a una PersonaContacto.
     */
    @Test
    @Transactional
    public void actualizarComercioDePersonaContacto() {
        // GIVEN
        Comercio comercio1 = crearYGuardarComercio("CIF008");
        Comercio comercio2 = crearYGuardarComercio2("CIF009");
        PersonaContacto personaContacto = new PersonaContacto("contacto@comercio.com");
        personaContacto.setComercio(comercio1);
        personaContactoRepository.save(personaContacto);
        comercio1.setPersonaContacto(personaContacto);
        comercioRepository.save(comercio1);

        // WHEN
        personaContacto.setComercio(comercio2);
        personaContactoRepository.save(personaContacto);
        comercio2.setPersonaContacto(personaContacto);
        comercioRepository.save(comercio2);

        // THEN
        PersonaContacto personaContactoBD = personaContactoRepository.findById(personaContacto.getId()).orElse(null);
        Comercio comercio1BD = comercioRepository.findById(comercio1.getId()).orElse(null);
        Comercio comercio2BD = comercioRepository.findById(comercio2.getId()).orElse(null);

        assertThat(personaContactoBD).isNotNull();
        assertThat(personaContactoBD.getComercio()).isEqualTo(comercio2BD);

        assertThat(comercio1BD).isNotNull();
        assertNull(comercio1BD.getPersonaContacto());

        assertThat(comercio2BD).isNotNull();
        assertThat(comercio2BD.getPersonaContacto()).isEqualTo(personaContactoBD);
    }

    /**
     * Test para eliminar el Comercio asociado a una PersonaContacto.
     */
    @Test
    @Transactional
    public void eliminarComercioDePersonaContacto() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF010");
        PersonaContacto personaContacto = new PersonaContacto("contacto@comercio.com");
        personaContacto.setComercio(comercio);
        personaContactoRepository.save(personaContacto);
        comercio.setPersonaContacto(personaContacto);
        comercioRepository.save(comercio);

        // WHEN
        personaContacto.setComercio(null);
        personaContactoRepository.save(personaContacto);
        comercio.setPersonaContacto(null);
        comercioRepository.save(comercio);
        personaContactoRepository.delete(personaContacto);

        // THEN
        PersonaContacto personaContactoBD = personaContactoRepository.findById(personaContacto.getId()).orElse(null);
        Comercio comercioBD = comercioRepository.findById(comercio.getId()).orElse(null);

        assertThat(comercioBD).isNotNull();
        assertNull(comercioBD.getPersonaContacto());

        assertThat(personaContactoBD).isNull();
    }

    /**
     * Test para verificar que al asociar un Comercio a una PersonaContacto, se mantiene la consistencia bidireccional.
     */
    @Test
    @Transactional
    public void asociarComercioAPersonaContactoConsistente() {
        // GIVEN
        Comercio comercio = crearYGuardarComercio("CIF011");
        PersonaContacto personaContacto = new PersonaContacto("contacto@comercio.com");

        // WHEN
        personaContacto.setComercio(comercio);
        personaContactoRepository.save(personaContacto);
        comercio.setPersonaContacto(personaContacto);
        comercioRepository.save(comercio);

        // THEN
        Comercio comercioBD = comercioRepository.findById(comercio.getId()).orElse(null);
        PersonaContacto personaContactoBD = personaContactoRepository.findById(personaContacto.getId()).orElse(null);

        assertThat(comercioBD).isNotNull();
        assertThat(comercioBD.getPersonaContacto()).isNotNull();
        assertThat(comercioBD.getPersonaContacto()).isEqualTo(personaContactoBD);

        assertThat(personaContactoBD).isNotNull();
        assertThat(personaContactoBD.getComercio()).isEqualTo(comercioBD);
    }


}
