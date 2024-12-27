// test/repository/ComercioTest.java

package interconnection.repository;

import interconnection.model.Comercio;
import interconnection.model.TipoUsuario;
import interconnection.model.Usuario;
import interconnection.model.Pais;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "/sql/clean-test-db.sql")
public class TipoUsuarioTest {

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaisRepository paisRepository;

    // Métodos auxiliares para reducir duplicación

    private TipoUsuario crearYGuardarTipoUsuario(String tipo) {

        TipoUsuario tipoUsuario = new TipoUsuario("default-type");
        tipoUsuarioRepository.save(tipoUsuario);

        Pais pais = new Pais("default-country");
        paisRepository.save(pais);

        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais);
        comercioRepository.save(comercio);

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


        tipoUsuario.getUsuarios().add(usuario);
        tipoUsuario.getUsuarios().add(usuario2);
        tipoUsuario.getUsuarios().add(usuario3);

        tipoUsuarioRepository.save(tipoUsuario);

        return tipoUsuario;
    }


    //
    // Tests modelo Comercio en memoria, sin la conexión con la BD
    //

    @Test
    public void crearTipoUsuario() {
        // GIVEN
        TipoUsuario tipoUsuario = new TipoUsuario("default-type");

        // WHEN && THEN
        assertThat(tipoUsuario.getNombre()).isEqualTo("default-type");
    }

    @Test
    public void comprobarIgualdadTipoUsuario() {
        // GIVEN
        TipoUsuario tipoUsuario1 = new TipoUsuario("default-type");
        TipoUsuario tipoUsuario2 = new TipoUsuario("default-type");
        TipoUsuario tipoUsuario3 = new TipoUsuario("default-type2");

        tipoUsuario1.setId(1L);
        tipoUsuario2.setId(1L);
        tipoUsuario3.setId(3L);
        // THEN
        assertThat(tipoUsuario1).isEqualTo(tipoUsuario2);
        assertThat(tipoUsuario1).isNotEqualTo(tipoUsuario3);
    }

    @Test
    @Transactional
    public void buscarTipoUsuarioPorNombre() {
        // GIVEN
        crearYGuardarTipoUsuario("default-type");

        // WHEN
        TipoUsuario tipoUsuarioBD = tipoUsuarioRepository.findByNombre("default-type").orElse(null);
        // THEN
        assertThat(tipoUsuarioBD.getNombre()).isEqualTo("default-type");
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // GIVEN
        TipoUsuario tipoUsuario = crearYGuardarTipoUsuario("default-type");

        // WHEN
        TipoUsuario tipoUsuarioDB = tipoUsuarioRepository.findByNombre("default-type").orElse(null);
        Usuario usuarioDB = tipoUsuarioDB.getUsuarios().iterator().next();
        usuarioDB.setNombre("Usuario Uno Modificado");

        usuarioDB = usuarioRepository.findById(usuarioDB.getId()).orElse(null);

        // THEN
        assertThat(usuarioDB.getNombre()).isEqualTo("Usuario Uno Modificado");
    }



}
