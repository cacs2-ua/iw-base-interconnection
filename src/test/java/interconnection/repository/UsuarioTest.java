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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class UsuarioTest {

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
    private ValoracionTecnicoRepository valoracionTecnicoRepository;

    private Usuario crearYGuardarUsuario(String email1) {
        Pais pais = new Pais("default-country");
        paisRepository.save(pais);


        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais); // Asocia el país al comercio
        comercioRepository.save(comercio); // Guardar primero el comercio

        TipoUsuario tipoUsuario = new TipoUsuario("default-type");
        tipoUsuarioRepository.save(tipoUsuario);

        Usuario usuario = new Usuario(email1);
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

        EstadoIncidencia estadoIncidencia = new EstadoIncidencia("default-state");
        estadoIncidenciaRepository.save(estadoIncidencia);

        Incidencia incidencia = new Incidencia("default-title");
        incidencia.setUsuario_comercio(usuario);
        incidencia.setUsuario_tecnico(usuario2);
        incidencia.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia);

        Incidencia incidencia2 = new Incidencia("default-title2");
        incidencia2.setUsuario_tecnico(usuario);
        incidencia2.setUsuario_comercio(usuario2);
        incidencia2.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia2);

        Incidencia incidencia3 = new Incidencia("default-title3");
        incidencia3.setUsuario_comercio(usuario);
        incidencia3.setUsuario_tecnico(usuario2);
        incidencia3.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia3);

        Incidencia incidencia4 = new Incidencia("default-title2");
        incidencia4.setUsuario_tecnico(usuario);
        incidencia4.setUsuario_comercio(usuario2);
        incidencia4.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia4);

        usuario.addIncidencia_comercio(incidencia);
        usuario.addIncidencia_comercio(incidencia3);
        usuario.addIncidencia_tecnico(incidencia2);
        usuario.addIncidencia_tecnico(incidencia4);

        usuarioRepository.save(usuario);

        usuarioRepository.save(usuario);

        //AQUÍ FALLA
        ValoracionTecnico valoracionTecnico = new ValoracionTecnico(4.0);
        valoracionTecnicoRepository.save(valoracionTecnico);
        usuario.setValoracionTecnico(valoracionTecnico);
        usuarioRepository.save(usuario);

        valoracionTecnico.setTecnico(usuario);

        usuarioRepository.save(usuario);
        valoracionTecnicoRepository.save(valoracionTecnico);

        return usuario;
    }

    //
    // Tests modelo Usuario en memoria, sin la conexión con la BD
    //

    @Test
    public void crearUsuario() {
        // GIVEN
        Usuario usuario = new Usuario("user@comercio.com");

        // THEN
        assertThat(usuario.getEmail()).isEqualTo("user@comercio.com");
        assertThat(usuario.getNombre()).isEqualTo("default");
        assertThat(usuario.getContrasenya()).isEqualTo("default");
    }


    @Test
    public void comprobarIgualdadUsuariosSinId() {
        // GIVEN
        Usuario usuario1 = new Usuario("user@comercio.com");
        Usuario usuario2 = new Usuario("user@comercio.com");
        Usuario usuario3 = new Usuario("user2@comercio.com");

        // THEN
        assertThat(usuario1).isEqualTo(usuario2);
        assertThat(usuario1).isNotEqualTo(usuario3);
    }

    @Test
    public void comprobarIgualdadUsuariosConId() {
        // GIVEN
        Usuario usuario1 = new Usuario("user1@comercio.com");
        Usuario usuario2 = new Usuario("user2@comercio.com");
        Usuario usuario3 = new Usuario("user3@comercio.com");

        usuario1.setId(1L);
        usuario2.setId(2L);
        usuario3.setId(1L);

        // THEN
        assertThat(usuario1).isEqualTo(usuario3);
        assertThat(usuario1).isNotEqualTo(usuario2);
    }

    //
    // Tests UsuarioRepository.
    //

    @Test
    @Transactional
    public void crearYBuscarUsuarioBaseDatos() {
        // GIVEN
        Usuario usuario = crearYGuardarUsuario("user@comercio.com");

        // THEN
        Usuario usuarioBD = usuarioRepository.findById(usuario.getId()).orElse(null);
        assertThat(usuarioBD).isNotNull();
        assertThat(usuarioBD.getEmail()).isEqualTo("user@comercio.com");
        assertThat(usuarioBD.getNombre()).isEqualTo("default");
        assertThat(usuarioBD.getComercio().getNombre()).isEqualTo("default-name");
    }


    @Test
    @Transactional
    public void buscarUsuarioPorEmail() {
        // GIVEN
        crearYGuardarUsuario("user@comercio.com");

        // WHEN
        Usuario usuarioBD = usuarioRepository.findByEmail("user@comercio.com").orElse(null);

        // THEN
        assertThat(usuarioBD.getNombre()).isEqualTo("default");
    }

    @Test
    @Transactional
    public void salvarUsuarioEnBaseDatosConComercioNoBDLanzaExcepcion() {
        // GIVEN
        // Un usuario nuevo que no está en la BD
        // y una tarea asociada a ese usuario,

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Comercio comercio = new Comercio("comercio 1");

        // WHEN // THEN
        // se lanza una excepción al intentar salvar la tarea en la BD

        Assertions.assertThrows(Exception.class, () -> {
            usuarioRepository.save(usuario);
        });
    }

    @Test
    @Transactional
    public void salvarUsuarioEnBaseDatosConTipoUsuarioNoBDLanzaExcepcion() {
        // GIVEN
        // Un usuario nuevo que no está en la BD
        // y una tarea asociada a ese usuario,

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        TipoUsuario tipoUsuario = new TipoUsuario("default");

        // WHEN // THEN
        // se lanza una excepción al intentar salvar la tarea en la BD

        Assertions.assertThrows(Exception.class, () -> {
            usuarioRepository.save(usuario);
        });
    }

    @Test
    @Transactional
    public void unUsuarioTecnicoTieneUnaListaDeIncidencias() {
        // GIVEN
        Usuario usuario = crearYGuardarUsuario("tecnico@empresa.com");

        // WHEN
        Usuario usuarioRecuperado = usuarioRepository.findById(usuario.getId()).orElse(null);

        // THEN
        assertThat(usuarioRecuperado).isNotNull();
        assertThat(usuarioRecuperado.getIncidencias_tecnico()).hasSize(2);
    }

    @Test
    @Transactional
    public void unUsuarioComercioTieneUnaListaDeIncidencias() {
        // GIVEN
        Usuario usuario = crearYGuardarUsuario("comercio@empresa.com");

        // WHEN
        Usuario usuarioRecuperado = usuarioRepository.findById(usuario.getId()).orElse(null);

        // THEN
        assertThat(usuarioRecuperado).isNotNull();
        assertThat(usuarioRecuperado.getIncidencias_comercio()).hasSize(2);
    }

    /**
     * Test para verificar que un Usuario puede existir sin una ValoracionTecnico asociada.
     */
    @Test
    @Transactional
    public void testUsuarioSinValoracionTecnico() {
        // Crear y guardar un usuario sin valoración técnica
        Usuario usuario = crearYGuardarUsuario("test-email-sin-valoracion@example.com");
        usuario.setValoracionTecnico(null);
        usuarioRepository.save(usuario);

        // Recuperar el usuario desde la base de datos
        Usuario usuarioRecuperado = usuarioRepository.findById(usuario.getId()).orElse(null);

        // Verificar que el usuario existe y no tiene valoración técnica
        assertThat(usuarioRecuperado).isNotNull();
        assertNull(usuarioRecuperado.getValoracionTecnico());
    }

    /**
     * Test para verificar que un Usuario puede tener una ValoracionTecnico asociada correctamente.
     */
    @Test
    @Transactional
    public void testUsuarioConValoracionTecnico() {
        // Crear y guardar un usuario
        Usuario usuario = crearYGuardarUsuario("test-email-con-valoracion@example.com");

        // Crear y guardar una valoración técnica
        ValoracionTecnico valoracionTecnico = new ValoracionTecnico(4.5f);
        valoracionTecnicoRepository.save(valoracionTecnico);

        // Asignar la valoración técnica al usuario
        usuario.setValoracionTecnico(valoracionTecnico);
        usuarioRepository.save(usuario);

        // Recuperar el usuario desde la base de datos
        Usuario usuarioRecuperado = usuarioRepository.findById(usuario.getId()).orElse(null);
        ValoracionTecnico valoracionRecuperada = usuarioRecuperado.getValoracionTecnico();

        // Verificar que la valoración técnica está correctamente asignada
        assertThat(usuarioRecuperado).isNotNull();
        assertThat(valoracionRecuperada).isNotNull();
        assertThat(valoracionRecuperada.getValoracion()).isEqualTo(4.5f);
        assertThat(valoracionRecuperada.getTecnico()).isEqualTo(usuarioRecuperado);
    }

    /**
     * Test para verificar la actualización de la ValoracionTecnico de un Usuario.
     */
    @Test
    @Transactional
    public void testActualizarValoracionTecnico() {
        // Crear y guardar un usuario
        Usuario usuario = crearYGuardarUsuario("test-email-actualizar-valoracion@example.com");

        // Crear y guardar una valoración técnica inicial
        ValoracionTecnico valoracionInicial = new ValoracionTecnico(3.0f);
        valoracionTecnicoRepository.save(valoracionInicial);

        // Asignar la valoración técnica inicial al usuario
        usuario.setValoracionTecnico(valoracionInicial);
        usuarioRepository.save(usuario);

        // Crear y guardar una nueva valoración técnica
        ValoracionTecnico valoracionNueva = new ValoracionTecnico(5.0f);
        valoracionTecnicoRepository.save(valoracionNueva);

        // Actualizar la valoración técnica del usuario
        usuario.setValoracionTecnico(valoracionNueva);
        usuarioRepository.save(usuario);

        // Recuperar el usuario desde la base de datos
        Usuario usuarioRecuperado = usuarioRepository.findById(usuario.getId()).orElse(null);

        // Verificar que la valoración técnica se ha actualizado correctamente
        assertThat(usuarioRecuperado).isNotNull();
        ValoracionTecnico valoracionRecuperada = usuarioRecuperado.getValoracionTecnico();
        assertThat(valoracionRecuperada).isNotNull();
        assertThat(valoracionRecuperada.getValoracion()).isEqualTo(5.0f);
        assertThat(valoracionRecuperada.getTecnico()).isEqualTo(usuarioRecuperado);

        // Verificar que la valoración técnica inicial ya no está asociada al usuario
        ValoracionTecnico valoracionInicialRecuperada = valoracionTecnicoRepository.findById(valoracionInicial.getId()).orElse(null);
        assertThat(valoracionInicialRecuperada).isNotNull();
    }

    /**
     * Test para verificar la eliminación de una ValoracionTecnico asociada a un Usuario.
     */
    @Test
    @Transactional
    public void testEliminarValoracionTecnico() {
        // Crear y guardar un usuario
        Usuario usuario = crearYGuardarUsuario("test-email-eliminar-valoracion@example.com");

        // Crear y guardar una valoración técnica
        ValoracionTecnico valoracionTecnico = new ValoracionTecnico(4.0f);
        valoracionTecnicoRepository.save(valoracionTecnico);

        // Asignar la valoración técnica al usuario
        usuario.setValoracionTecnico(valoracionTecnico);
        usuarioRepository.save(usuario);

        // Eliminar la valoración técnica
        usuario.setValoracionTecnico(null);
        usuarioRepository.save(usuario);
        valoracionTecnicoRepository.delete(valoracionTecnico);

        // Recuperar el usuario desde la base de datos
        Usuario usuarioRecuperado = usuarioRepository.findById(usuario.getId()).orElse(null);

        // Verificar que la valoración técnica ha sido eliminada y la relación está nula
        assertThat(usuarioRecuperado).isNotNull();
        assertNull(usuarioRecuperado.getValoracionTecnico());

        // Verificar que la valoración técnica ya no existe en la base de datos
        ValoracionTecnico valoracionRecuperada = valoracionTecnicoRepository.findById(valoracionTecnico.getId()).orElse(null);
        assertThat(valoracionRecuperada).isNull();
    }


}
