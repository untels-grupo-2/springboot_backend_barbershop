package com.diamondbarbershop.apibarbershop.security.config;

import com.diamondbarbershop.apibarbershop.security.jwt.JwtAuthenticationEntryPoint;
import com.diamondbarbershop.apibarbershop.security.util.ConstantesSeguridad;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

//Le indica al contenedor de spring que esta es una clase de seguridad al momento de arrancar la aplicación
@Configuration
//Indicamos que se activa la seguridad web en nuestra aplicación y además esta será una clase la cual contendrá toda la configuración referente a la seguridad
@EnableWebSecurity
public class SecurityConfig {

    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    //Este bean va a encargarse de verificar la información de los usuarios que se loguearán en nuestra api
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //Con este bean nos encargaremos de encriptar todas nuestras contraseñas
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Decodificador de JWT — Spring Security lo usa automáticamente para
     * validar cada token entrante. Reemplaza al JwtAuthenticationFilter manual.
     *
     * MacAlgorithm.HS512 debe coincidir con el algoritmo usado en JwtGenerador.
     */
    @Bean
    NimbusJwtDecoder jwtDecoder() {
        SecretKey key = Keys.hmacShaKeyFor(
                ConstantesSeguridad.JWT_FIRMA.getBytes(StandardCharsets.UTF_8)
        );
        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                .build();
    }

    /**
     * Convierte los claims del JWT en GrantedAuthorities de Spring Security.
     *
     * El JWT tiene un claim "rol" con valor "ADMIN" o "USER".
     * Sin este converter, Spring Security buscaría "scope" o "scp" y añadiría
     * el prefijo "SCOPE_" — lo que rompería nuestros hasAuthority("ADMIN").
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");
            if (rol == null || rol.isBlank()) return List.of();
            return List.of(new SimpleGrantedAuthority(rol));
        });
        return converter;
    }

    //Vamos a crear un bean el cual va a establecer una cadena de filtros de seguridad en nuestra aplicación.
    // Y es aquí donde determinaremos los permisos segun los roles de usuarios para acceder a nuestra aplicación
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/autenticacion/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/autenticacion/bootstrap/admin").permitAll()
                        .requestMatchers(HttpMethod.POST, "/email/password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/autenticacion/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/autenticacion/refresh-token").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/autenticacion/registro/cliente").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/autenticacion/registro/admin").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/autenticacion/logout").authenticated()

                        // Servicios
                        .requestMatchers(HttpMethod.POST, "/servicios").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/servicios", "/servicios/*").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/servicios/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/servicios/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/servicios/tipos").hasAuthority("ADMIN")

                        // Barberos
                        .requestMatchers(HttpMethod.POST, "/barberos").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/barberos", "/barberos/*").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/barberos/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/barberos/*").hasAuthority("ADMIN")

                        // Usuarios
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuarios/me").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/usuarios/*").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/me").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/*").hasAnyAuthority("ADMIN", "USER")

                        // Valoraciones
                        .requestMatchers(HttpMethod.POST, "/valoraciones").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/valoraciones").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/valoraciones/*/estado").hasAuthority("ADMIN")

                        // Horarios base
                        .requestMatchers(HttpMethod.PUT, "/horarios-base").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/horarios-base/confirmacion").hasAuthority("ADMIN")

                        // Horarios semana
                        .requestMatchers(HttpMethod.GET, "/horarios-semana").hasAnyAuthority("ADMIN", "USER")

                        // Rangos horario
                        .requestMatchers(HttpMethod.GET, "/rangos-horario", "/rangos-horario/*").hasAnyAuthority("ADMIN", "USER")

                        // Reporte horarios
                        .requestMatchers(HttpMethod.GET, "/reporte/horarios").hasAuthority("ADMIN")

                        // Reservas
                        .requestMatchers(HttpMethod.POST, "/reservas").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/reservas/barberos-disponibles").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/reservas/*/comprobante").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/reservas/admin").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/reservas/*/estado").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reservas/mis-reservas").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/reservas/recompensa/estado").hasAuthority("USER")
                        .requestMatchers(HttpMethod.POST, "/reservas/recompensa").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/reservas/reportes").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                // OAuth2 Resource Server reemplaza al JwtAuthenticationFilter manual.
                // Spring Security valida la firma, expiración y claims del JWT automáticamente.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );
        // Ya NO hay http.addFilterBefore(jwtAuthenticationFilter(), ...)
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:5174"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(false); // JWT por header, normalmente false

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
