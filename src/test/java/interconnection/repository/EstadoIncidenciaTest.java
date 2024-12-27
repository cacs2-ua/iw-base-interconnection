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
@Sql(scripts = "/sql/clean-test-db.sql")
public class EstadoIncidenciaTest {

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

    private EstadoIncidencia crearYGuardarEstadoIncidencia(String nombre) {
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

        EstadoIncidencia estadoIncidencia = new EstadoIncidencia(nombre);
        estadoIncidenciaRepository.save(estadoIncidencia);

        Incidencia incidencia = new Incidencia("default-title");
        incidencia.setUsuario_comercio(usuario);
        incidencia.setUsuario_tecnico(usuario2);
        incidencia.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia);

        Incidencia incidencia2 = new Incidencia("default-title2");
        incidencia2.setUsuario_comercio(usuario);
        incidencia2.setUsuario_tecnico(usuario2);
        incidencia2.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia2);

        Incidencia incidencia3 = new Incidencia("default-title2");
        incidencia3.setUsuario_comercio(usuario);
        incidencia3.setUsuario_tecnico(usuario2);
        incidencia3.setEstado(estadoIncidencia);

        incidenciaRepository.save(incidencia3);

        estadoIncidencia.addIncidencia(incidencia);
        estadoIncidencia.addIncidencia(incidencia2);
        estadoIncidencia.addIncidencia(incidencia3);

        estadoIncidenciaRepository.save(estadoIncidencia);

        return estadoIncidencia;
    }

    @Test
    public void crearEstadoIncidencia() {
        // GIVEN
        EstadoIncidencia estadoIncidencia = new EstadoIncidencia("pendiente");

        // THEN
        assertThat(estadoIncidencia.getNombre()).isEqualTo("pendiente");
    }

    @Test
    public void comprobarIgualdadEstadosIncidenciaConId() {
        // GIVEN
        EstadoIncidencia estado1 = new EstadoIncidencia("pendiente");
        EstadoIncidencia estado2 = new EstadoIncidencia("completado");
        EstadoIncidencia estado3 = new EstadoIncidencia("pendiente");

        estado1.setId(1L);
        estado2.setId(2L);
        estado3.setId(1L);

        // THEN
        assertThat(estado1).isEqualTo(estado3);
        assertThat(estado1).isNotEqualTo(estado2);
    }

    //
    // Tests EstadoIncidenciaRepository.
    //

    @Test
    @Transactional
    public void crearYBuscarEstadoIncidenciaBaseDatos() {
        // GIVEN
        EstadoIncidencia estadoIncidencia = crearYGuardarEstadoIncidencia("pendiente");

        // THEN
        EstadoIncidencia estadoIncidenciaBD = estadoIncidenciaRepository.findById(estadoIncidencia.getId()).orElse(null);
        assertThat(estadoIncidenciaBD).isNotNull();
        assertThat(estadoIncidenciaBD.getNombre()).isEqualTo("pendiente");
    }

    @Test
    @Transactional
    public void salvarEstadoIncidenciaEnBaseDatosSinIncidencias() {
        // GIVEN
        EstadoIncidencia estadoIncidencia = new EstadoIncidencia("nuevo-estado");
        // No se asocian incidencias

        // WHEN
        EstadoIncidencia estadoIncidenciaGuardado = estadoIncidenciaRepository.save(estadoIncidencia);

        // THEN
        assertThat(estadoIncidenciaGuardado.getId()).isNotNull();
        assertThat(estadoIncidenciaGuardado.getNombre()).isEqualTo("nuevo-estado");
        assertThat(estadoIncidenciaGuardado.getIncidencias()).isEmpty();
    }

    @Test
    @Transactional
    public void unEstadoIncidenciaTieneUnaListaDeIncidencias() {
        // GIVEN
        EstadoIncidencia estadoIncidencia = crearYGuardarEstadoIncidencia("estado-multiple-incidencias");

        // WHEN
        EstadoIncidencia estadoIncidenciaRecuperado = estadoIncidenciaRepository.findById(estadoIncidencia.getId()).orElse(null);

        // THEN
        assertThat(estadoIncidenciaRecuperado).isNotNull();
        assertThat(estadoIncidenciaRecuperado.getIncidencias()).hasSize(3);
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // GIVEN
        EstadoIncidencia estadoIncidencia = crearYGuardarEstadoIncidencia("estado-original");

        // WHEN
        EstadoIncidencia estadoIncidenciaBD = estadoIncidenciaRepository.findById(estadoIncidencia.getId()).orElse(null);
        estadoIncidenciaBD.setNombre("Nombre Modificado");
        estadoIncidenciaRepository.save(estadoIncidenciaBD);

        // Recuperar el EstadoIncidencia nuevamente para verificar el cambio
        EstadoIncidencia estadoIncidenciaModificado = estadoIncidenciaRepository.findById(estadoIncidenciaBD.getId()).orElse(null);

        // THEN
        assertThat(estadoIncidenciaModificado).isNotNull();
        assertThat(estadoIncidenciaModificado.getNombre()).isEqualTo("Nombre Modificado");
    }



}
