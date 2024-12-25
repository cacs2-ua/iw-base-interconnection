package interconexion.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/pago/**")) // Ignorar CSRF en H2 Console y pagos
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // Permitir iframes para H2 Console
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // Permitir acceso sin autenticación a H2 Console
                        .requestMatchers("/pago/**").authenticated() // Requiere autenticación para los endpoints de pago
                        .anyRequest().permitAll() // Permitir acceso al resto de rutas sin autenticación
                )
                .httpBasic(Customizer.withDefaults()); // Configuración básica para autenticación

        return http.build();
    }
}
