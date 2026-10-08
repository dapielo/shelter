package com.david.shelter.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration // Esto sirve para convertirlo en un Bean y para buscar tambien Beans dentro de esta clase, para poder gestionar Spring el ciclo de vida de estos objetos
@EnableWebSecurity // Esto para habilitar esta configuracion a nivel de peticiones HTTP
@EnableMethodSecurity // Esto para habilitarla a nivel de metodos en los controladores
@RequiredArgsConstructor 
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // Definimos como Bean para poder inyectarlo en los servicios que lo necesiten
    @Bean 
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Este bean es el que le permite a spring crear la cadena de filtros de seguridad
    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        httpSecurity
            .cors(cors -> cors.configurationSource(corsConfiguration()))
            // desahabilitamos el csfr porque es una API REST
            .csrf(csfr -> csfr.disable()) 
            // que por defecto si se hace una peticion sin el permiso se devuelva el codigo de error 403 - Prohibido.
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
            // deshabilitamos la gestion de sesiones por lo mismo, en una API REST no hay sesiones
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Aqui vamos añadiendo todos los endpoints que tenemos e indicamos sus permisos
                .requestMatchers("/shelter/login/**").permitAll()
                .requestMatchers("/shelter/register/**").permitAll()
                .requestMatchers("/shelter/fosterhome/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/shelter/pet/**").permitAll() // Si es GET permitimos
                .requestMatchers("/shelter/pet/**").authenticated() // Si es cualquier otro verbo tiene que estar autenticado
                .requestMatchers("/shelter/fosterhome/**").authenticated()
                .requestMatchers("/shelter/adoption/**").authenticated()
                .requestMatchers("/shelter/user/**").authenticated()
                .anyRequest().authenticated()
            );

        // Añadimos el filtro de JWT que hemos creado antes del filtro por defecto de UsernamePassword de Spring Security, que en esta API no se usa
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean 
    // Este bean se usa para la configuracion de Cross Origin Resource Sharing, que es una politica que bloquea
    // cualquier peticion que no venga del mismo origen, para el navegador dos puertos del mismo ordenador, aunque sea en localhost
    // son dos origenes diferentes, asi que tenemos uqe dar persmisos
    public CorsConfigurationSource corsConfiguration() {

        // Establecemos los origenes metodos y cabeceras admitidos en un objeto de configuracion
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOrigins(List.of("http://127.0.0.1:4200","http://localhost:4200"));
        conf.setAllowedMethods(List.of("GET","PUT","POST","PATCH","DELETE"));
        conf.setAllowedHeaders(List.of("*"));

        // aqui establecemos las rutas de nuestra api a la que aplica la configuracion anterior, en este caso a todas "/**"
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}
