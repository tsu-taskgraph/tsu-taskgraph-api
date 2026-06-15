package ru.tsu_taskgraph.core_api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.tsu_taskgraph.core_api.exception.AuthenticationException;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final HandlerExceptionResolver exceptionResolver;

    public SecurityConfig(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
            JwtFilter jwtFilter
    ) {
        this.exceptionResolver = exceptionResolver;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(request -> corsConfig()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions -> exceptions
                        // Обработка отсутствия аутентификации
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Пробрасываем исключение в @RestControllerAdvice
                            exceptionResolver.resolveException(
                                    request, response, null, new AuthenticationException(authException.getMessage())
                            );
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        //swagger
                        .requestMatchers(
                                "/api/v3/api-docs/**", "/api/v1/swagger-ui/**", "/api/v1/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/test").authenticated()

                        //auth
                        .requestMatchers(
                                "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/logout").authenticated()

                        //user
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me/avatar/*").permitAll()
                        .requestMatchers("/api/v1/users/me/**").authenticated()
                        .requestMatchers("/api/v1/ai-providers/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfiguration corsConfig() {
        var config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*"
        ));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        return config;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}