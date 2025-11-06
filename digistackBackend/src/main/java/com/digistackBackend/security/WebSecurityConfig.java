package com.digistackBackend.security;

import com.digistackBackend.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080","http://localhost:8082")); // or front end origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http)throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                // Public endpoints
                                .requestMatchers(
                                        "/api/users/login",
                                        "/api/users/register",
                                        "/api/users/me"
                                ).permitAll()

                                // Admin-only endpoints
                                .requestMatchers(
                                        "/api/monthly-usage/**",
                                        "/api/global/**",
                                        "/api/quota/**"
                                ).hasRole("ADMIN")

                                // User info, allow USER and ADMIN
                                .requestMatchers(
                                        "/api/users/{email}",
                                        "/api/users/{id}/email"
                                ).hasAnyRole("USER", "ADMIN")

                                // Quota and user-specific quota endpoints (protected)
                                .requestMatchers(
                                        "/api/quota/{userId}/**"
                                ).authenticated()

                                // Keyword endpoints: protected (USER or ADMIN)
                                .requestMatchers(
                                        "/api/keywords/**",
                                        "/api/external/**"
                                ).hasAnyRole("USER", "ADMIN")

                                // Catch-all
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(expectionHandling -> expectionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.error("Access denied: ", accessDeniedException);
                            accessDeniedException.printStackTrace();
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                ).build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setAuthoritiesMapper(grantedAuthorities -> grantedAuthorities);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
