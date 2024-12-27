package interconnection.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import interconnection.dto.ComercioData;
import interconnection.dto.PersonaContactoData;
import interconnection.model.Pais;
import interconnection.repository.PaisRepository;


@SpringBootTest
@Sql(scripts = "/sql/clean-test-db.sql")
public class ComercioServiceTest {

    @Autowired
    ComercioService comercioService;

    @Autowired
    PaisRepository paisRepository;

    private ComercioData crearComercio() {
        ComercioData nuevoComercio = new ComercioData();
        Pais nuevoPais = new Pais("España");

        if (paisRepository.findByNombre("España").isEmpty()) {
            paisRepository.save(nuevoPais);
        }

        nuevoComercio.setNombre("Comercio Test");
        nuevoComercio.setCif("A12345678");
        nuevoComercio.setPais("España");
        nuevoComercio.setProvincia("Madrid");
        nuevoComercio.setDireccion("Calle Falsa 123");
        nuevoComercio.setIban("ES6621000418401234567891");
        nuevoComercio.setUrl_back("http://callback.test");


        ComercioData comercio = comercioService.crearComercio(nuevoComercio);
        return comercio;
    }

    @Test
    public void crearRecuperarComercioTest() {
        ComercioData comercio = crearComercio();
        assertThat(comercio.getId()).isNotNull();

        ComercioData comercioBd = comercioService.recuperarComercio(comercio.getId());
        assertThat(comercioBd).isNotNull();
        assertThat(comercioBd.getNombre()).isEqualTo("Comercio Test");
    }

    @Test
    public void actualizarURLComercioTest() {
        String nuevaURL = "https://www.google.com/";
        ComercioData comercio = crearComercio();
        comercioService.actualizarURLComercio(comercio.getId(), nuevaURL);
        ComercioData comercioActualizado = comercioService.recuperarComercio(comercio.getId());
        assertThat(comercioActualizado.getUrl_back())
                .isEqualTo(nuevaURL);
    }

    @Test
    public void regenerarAPIKeyComercioTest() {
        ComercioData comercio = crearComercio();
        String antiguaApi = comercio.getApiKey();
        comercioService.regenerarAPIKeyComercio(comercio.getId());
        ComercioData comercioActualizado = comercioService.recuperarComercio(comercio.getId());
        assertThat(comercioActualizado.getApiKey())
                .isNotEqualTo(antiguaApi);
    }

    @Test
    public void crearRecuperarYAsignarPersonaContactoAComercioTest() {
        ComercioData comercio = crearComercio();
        PersonaContactoData personaContacto = new PersonaContactoData();
        personaContacto.setNombre("Persona Contacto");
        personaContacto.setEmail("email@email.com");
        personaContacto.setTelefono("12345678");
        personaContacto = comercioService.crearPersonaContacto(personaContacto);

        PersonaContactoData contactoRecuperadoPorId = comercioService.recuperarPersonaContactoById(personaContacto.getId());

        comercioService.asignarPersonaDeContactoAComercio(comercio.getId(), personaContacto.getId());
        PersonaContactoData contactoRecuperadoPorIdComercio = comercioService.recuperarPersonaContactoByComercioId(comercio.getId());

        assertThat(contactoRecuperadoPorId).isEqualTo(contactoRecuperadoPorIdComercio);
    }

    @Test
    public void cambiarEstadoComercioTest() {
        ComercioData comercio = crearComercio();

        assertThat(comercio.getActivo()).isFalse();

        comercioService.modificarEstadoComercio(comercio.getId(), true);
        comercio = comercioService.recuperarComercio(comercio.getId());

        assertThat(comercio.getActivo()).isTrue();
    }

}
