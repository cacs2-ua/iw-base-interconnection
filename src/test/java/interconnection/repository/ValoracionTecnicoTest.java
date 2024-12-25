// test/repository/UsuarioTest.java

package interconnection.repository;

import interconnection.model.*;
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
public class ValoracionTecnicoTest {

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



    private ValoracionTecnico crearYGuardarValoracionTecnico(double valoracion) {
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

        ValoracionTecnico valoracionTecnico = new ValoracionTecnico(valoracion);
        valoracionTecnicoRepository.save(valoracionTecnico);
        usuario.setValoracionTecnico(valoracionTecnico);
        usuarioRepository.save(usuario);

        valoracionTecnico.setTecnico(usuario);

        usuarioRepository.save(usuario);
        valoracionTecnicoRepository.save(valoracionTecnico);

        return valoracionTecnico;
    }

    /**
     * Test para verificar que una ValoracionTecnico puede existir sin un Usuario asociado.
     */
    @Test
    @Transactional
    public void testValoracionTecnicoSinUsuario() {
        // Crear y guardar una valoración técnica sin asignar a ningún usuario
        ValoracionTecnico valoracion = new ValoracionTecnico(4.0f);
        valoracionTecnicoRepository.save(valoracion);

        // Recuperar la valoración desde la base de datos
        ValoracionTecnico valoracionRecuperada = valoracionTecnicoRepository.findById(valoracion.getId()).orElse(null);

        Usuario usuario = valoracionRecuperada.getTecnico();

        // Verificar que la valoración existe y no tiene asociado ningún usuario
        assertThat(valoracionRecuperada).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("default");
    }

    /**
     * Test para verificar que una ValoracionTecnico puede estar asociada a un Usuario correctamente.
     */
    @Test
    @Transactional
    public void testValoracionTecnicoConUsuario() {
        // Crear y guardar una valoración técnica con un usuario
        ValoracionTecnico valoracion = crearYGuardarValoracionTecnico(5.0f);

        // Recuperar la valoración desde la base de datos
        ValoracionTecnico valoracionRecuperada = valoracionTecnicoRepository.findById(valoracion.getId()).orElse(null);

        // Verificar que la valoración técnica está correctamente asociada al usuario
        assertThat(valoracionRecuperada).isNotNull();
        assertThat(valoracionRecuperada.getValoracion()).isEqualTo(5.0f);
        assertThat(valoracionRecuperada.getTecnico()).isNotNull();
        assertThat(valoracionRecuperada.getTecnico().getValoracionTecnico()).isEqualTo(valoracionRecuperada);
    }

    /**
     * Test para verificar la eliminación de una ValoracionTecnico y la correcta actualización de la relación con Usuario.
     */
    @Test
    @Transactional
    public void testEliminarValoracionTecnico() {
        // Crear y guardar una valoración técnica con un usuario
        ValoracionTecnico valoracion = crearYGuardarValoracionTecnico(4.5f);

        // Eliminar la valoración técnica
        valoracionTecnicoRepository.delete(valoracion);

        // Recuperar la valoración desde la base de datos
        ValoracionTecnico valoracionRecuperada = valoracionTecnicoRepository.findById(valoracion.getId()).orElse(null);

        // Verificar que la valoración ya no existe
        assertThat(valoracionRecuperada).isNull();

        // Recuperar el usuario y verificar que la relación está nula
        Usuario tecnico = usuarioRepository.findById(valoracion.getTecnico().getId()).orElse(null);
        assertThat(tecnico).isNotNull();
    }

}
