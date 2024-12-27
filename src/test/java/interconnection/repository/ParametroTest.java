package interconnection.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import interconnection.model.Parametro;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "/sql/clean-test-db.sql")
public class ParametroTest {

    @Autowired
    private ParametroRepository parametroRepository;

    @Test
    @Transactional
    public void crearYGuardarParametro() {
        // GIVEN
        Parametro parametro = new Parametro("nombreParametro", "valorParametro");

        // WHEN
        Parametro parametroGuardado = parametroRepository.save(parametro);

        // THEN
        assertThat(parametroGuardado).isNotNull();
        assertThat(parametroGuardado.getId()).isNotNull();
        assertThat(parametroGuardado.getNombre()).isEqualTo("nombreParametro");
        assertThat(parametroGuardado.getValor()).isEqualTo("valorParametro");
    }

    @Test
    @Transactional
    public void buscarParametroPorId() {
        // GIVEN
        Parametro parametro = new Parametro("parametroTest", "valorTest");
        Parametro parametroGuardado = parametroRepository.save(parametro);

        // WHEN
        Optional<Parametro> parametroEncontrado = parametroRepository.findById(parametroGuardado.getId());

        // THEN
        assertThat(parametroEncontrado).isPresent();
        assertThat(parametroEncontrado.get().getNombre()).isEqualTo("parametroTest");
        assertThat(parametroEncontrado.get().getValor()).isEqualTo("valorTest");
    }

    @Test
    @Transactional
    public void actualizarParametro() {
        // GIVEN
        Parametro parametro = new Parametro("parametroOriginal", "valorOriginal");
        Parametro parametroGuardado = parametroRepository.save(parametro);

        // WHEN
        parametroGuardado.setValor("valorActualizado");
        parametroRepository.save(parametroGuardado);

        // THEN
        Optional<Parametro> parametroActualizado = parametroRepository.findById(parametroGuardado.getId());
        assertThat(parametroActualizado).isPresent();
        assertThat(parametroActualizado.get().getValor()).isEqualTo("valorActualizado");
    }

    @Test
    @Transactional
    public void eliminarParametro() {
        // GIVEN
        Parametro parametro = new Parametro("parametroEliminar", "valorEliminar");
        Parametro parametroGuardado = parametroRepository.save(parametro);

        // WHEN
        parametroRepository.delete(parametroGuardado);

        // THEN
        Optional<Parametro> parametroEliminado = parametroRepository.findById(parametroGuardado.getId());
        assertThat(parametroEliminado).isNotPresent();
    }

    @Test
    @Transactional
    public void guardarParametroSinNombreLanzaExcepcion() {
        // GIVEN
        Parametro parametroSinNombre = new Parametro(null, "valor");

        // WHEN // THEN
        assertThrows(Exception.class, () -> {
            parametroRepository.save(parametroSinNombre);
        });
    }

    @Test
    @Transactional
    public void guardarParametroSinValorLanzaExcepcion() {
        // GIVEN
        Parametro parametroSinValor = new Parametro("parametroSinValor", null);

        // WHEN // THEN
        assertThrows(Exception.class, () -> {
            parametroRepository.save(parametroSinValor);
        });
    }
}
