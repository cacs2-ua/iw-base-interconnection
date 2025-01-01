// RUTA: src/main/java/interconnection/config/AppConfig.java

package interconnection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

// NUEVO: import
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.DefaultResponseErrorHandler;

@Configuration
public class AppConfig {

    /**
     * Bean de RestTemplate para realizar solicitudes HTTP.
     * Desactivamos el error handler por defecto para manejar manualmente los status 4xx/5xx.
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // NUEVO: Desactivamos o personalizamos el error handler
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                // forzamos a "no error" para que no lance excepción
                return false;
            }
        });

        return restTemplate;
    }
}
