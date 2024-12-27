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
public class MensajeTest {

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private EstadoIncidenciaRepository estadoIncidenciaRepository;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    private Mensaje crearYGuardarMensaje(String contenido) {

        Pais pais = new Pais("default-country");
        paisRepository.save(pais);


        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

        TipoUsuario tipoUsuario = new TipoUsuario("default-type");
        tipoUsuarioRepository.save(tipoUsuario);


        Usuario usuario = new Usuario("default-email1");
        usuario.setTipo(tipoUsuario);
        usuario.setComercio(comercio);
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario("default-email2");
        usuario2.setTipo(tipoUsuario);
        usuario2.setComercio(comercio);
        usuarioRepository.save(usuario2);

        Usuario usuario3 = new Usuario("default-email3");
        usuario3.setTipo(tipoUsuario);
        usuario3.setComercio(comercio);
        usuarioRepository.save(usuario3);


        comercio.getUsuarios().add(usuario);
        comercio.getUsuarios().add(usuario2);
        comercio.getUsuarios().add(usuario3);

        comercioRepository.save(comercio);

        EstadoIncidencia estadoIncidencia = new EstadoIncidencia("default");

        estadoIncidenciaRepository.save(estadoIncidencia);


        Incidencia incidencia = new Incidencia("titulo");
        incidencia.setUsuario_comercio(usuario);
        incidencia.setUsuario_tecnico(usuario2);
        incidencia.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia);

        Mensaje mensaje = new Mensaje("mensaje");
        mensaje.setIncidencia(incidencia);
        mensaje.setUsuario(usuario);

        mensajeRepository.save(mensaje);

        return mensaje;
    }

    //
    // Tests modelo Usuario en memoria, sin la conexión con la BD
    //

    @Test
    public void crearMensaje() {
        // GIVEN
        Mensaje mensaje = new Mensaje("mensaje");

        // THEN
        assertThat(mensaje.getContenido()).isEqualTo("mensaje");
    }

    @Test
    public void comprobarIgualdadMensajes() {
        // GIVEN
        Mensaje mensaje1 = new Mensaje("mensaje1");
        Mensaje mensaje2 = new Mensaje("mensaje2");
        Mensaje mensaje3 = new Mensaje("mensaje3");

        mensaje1.setId(1L);
        mensaje2.setId(2L);
        mensaje3.setId(1L);

        // THEN
        assertThat(mensaje1).isEqualTo(mensaje3);
        assertThat(mensaje1).isNotEqualTo(mensaje2);
    }

    @Test
    @Transactional
    public void crearYBuscarMensajeBaseDatos() {
        // GIVEN
        Mensaje mensaje = crearYGuardarMensaje("mensaje");

        // WHEN && THEN
        Mensaje mensajeDB = mensajeRepository.findById(mensaje.getId()).orElse(null);

        assertThat(mensajeDB).isNotNull();
        assertThat(mensajeDB.getContenido()).isEqualTo("mensaje");
        assertThat(mensajeDB.getIncidencia().getTitulo()).isEqualTo("titulo");
    }

    @Test
    @Transactional
    public void buscarMensajePorId() {
        // GIVEN
        crearYGuardarMensaje("mensaje");

        // WHEN
        Mensaje mensajeDB = mensajeRepository.findById(1L).orElse(null);

        // THEN
        assertThat(mensajeDB.getContenido()).isEqualTo("mensaje");
    }

    @Test
    @Transactional
    public void salvarMensajeEnBaseDatosConUsuarioNoBDLanzaExcepcion() {
        // GIVEN

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Mensaje mensaje = new Mensaje("mensaje");

        // WHEN // THEN
        // se lanza una excepción al intentar salvar la tarea en la BD

        Assertions.assertThrows(Exception.class, () -> {
            mensajeRepository.save(mensaje);
        });
    }

    @Test
    @Transactional
    public void salvarMensajeEnBaseDatosConIncidenciaNoBDLanzaExcepcion() {
        // GIVEN
        // Un usuario nuevo que no está en la BD
        // y una tarea asociada a ese usuario,

        Incidencia incidencia = new Incidencia("titulo");
        Mensaje mensaje = new Mensaje("mensaje");

        // WHEN // THEN
        // se lanza una excepción al intentar salvar la tarea en la BD

        Assertions.assertThrows(Exception.class, () -> {
            mensajeRepository.save(mensaje);
        });
    }


}
