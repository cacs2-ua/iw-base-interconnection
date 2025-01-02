package interconnection.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/pago/**", "/tienda/**")) // Ignorar CSRF en H2 Console
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // Permitir iframes para H2 Console
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // Permitir acceso a la consola H2
                        .requestMatchers("/pago/**").authenticated() // Requiere autenticación para los endpoints de pago
                        .anyRequest().permitAll() // Permitir acceso al resto de rutas
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
