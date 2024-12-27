// test/repository/ComercioTest.java

package interconnection.repository;

import interconnection.model.Comercio;
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
public class PaisTest {

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaisRepository paisRepository;

    // Métodos auxiliares para reducir duplicación

    private Pais crearYGuardarPais(String nombre) {
        // Crear y guardar entidades dependientes
        Pais pais = new Pais(nombre);
        paisRepository.save(pais);


        // Crear el comercio y guardar antes de asociar
        Comercio comercio = new Comercio("default-cif");
        comercio.setPais_id(pais); // Asocia el país al comercio
        comercioRepository.save(comercio); // Guardar primero el comercio

        Comercio comercio2 = new Comercio("default-cif2");
        comercio2.setPais_id(pais); // Asocia el país al comercio
        comercioRepository.save(comercio2); // Guardar primero el comercio

        Comercio comercio3 = new Comercio("default-cif3");
        comercio3.setPais_id(pais); // Asocia el país al comercio
        comercioRepository.save(comercio3); // Guardar primero el comercio


        pais.addComercio(comercio);
        pais.addComercio(comercio2);
        pais.addComercio(comercio3);

        paisRepository.save(pais);

        return pais;
    }


    //
    // Tests modelo Comercio en memoria, sin la conexión con la BD
    //

    @Test
    public void crearPais() {
        // GIVEN
        Pais pais = new Pais("default-country");

        // WHEN && THEN
        assertThat(pais.getNombre()).isEqualTo("default-country");

    }

    @Test
    public void comprobarIgualdadPaises() {
        // GIVEN
        Pais pais1 = new Pais("default-country");
        Pais pais2 = new Pais("default-country2");
        Pais pais3 = new Pais("default-country");

        pais1.setId(1L);
        pais2.setId(2L);
        pais3.setId(1L);

        // THEN
        assertThat(pais1).isEqualTo(pais3);
        assertThat(pais1).isNotEqualTo(pais2);
    }

    @Test
    @Transactional
    public void crearYBuscarPaisBaseDatos() {
        // GIVEN
        Pais pais = crearYGuardarPais("default-country");

        // THEN
        assertThat(pais.getId()).isNotNull();

        Pais paisDB = paisRepository.findById(pais.getId()).orElse(null);
        assertThat(paisDB.getNombre()).isEqualTo("default-country");
    }

    @Test
    @Transactional
    public void buscarPaisPorNombre() {
        // GIVEN
        crearYGuardarPais("default-country");

        // WHEN
        Pais paisDB = paisRepository.findByNombre("default-country").orElse(null);

        // THEN
        assertThat(paisDB.getNombre()).isEqualTo("default-country");
    }

    @Test
    @Transactional
    public void unPaisTieneUnaListaDeComercios() {
        // GIVEN
        Pais pais = crearYGuardarPais("default-country");

        // WHEN
        Pais paisRecuperado = paisRepository.findById(pais.getId()).orElse(null);

        // THEN
        assertThat(paisRecuperado.getComercios()).hasSize(3);
    }

    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // GIVEN
        Pais pais = crearYGuardarPais("default-country");

        // WHEN
        Pais paisDB = paisRepository.findById(pais.getId()).orElse(null);
        Comercio comercioDB = paisDB.getComercios().iterator().next();
        comercioDB.setCif("CIF123486");

        comercioDB = comercioRepository.findById(comercioDB.getId()).orElse(null);

        // THEN
        assertThat(comercioDB.getCif()).isEqualTo("CIF123486");
    }

}
