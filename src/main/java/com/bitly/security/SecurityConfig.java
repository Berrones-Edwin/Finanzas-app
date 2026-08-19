package com.bitly.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

        private final JwtFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

                httpSecurity
                                .cors(cors -> cors.configurationSource(configurationSource()))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(
                                                req -> req
                                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                                .requestMatchers("/api/v1/auth/logout").authenticated()
                                                                .requestMatchers(
                                                                                "/api/v1/auth/**",
                                                                                "/auth/**",
                                                                                "/v2/api-docs",
                                                                                "/v3/api-docs/**",
                                                                                "/v3/api-docs", "/v2/api-docs/**",
                                                                                "/swagger-resources",
                                                                                "/swagger-resources/**",
                                                                                "/configuration/ui",
                                                                                "/configuration/security",
                                                                                "/swagger-ui/**", "/webjars/**",
                                                                                "/swagger-ui.html")
                                                                .permitAll().anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return httpSecurity.build();
        }

        @Bean
        public CorsConfigurationSource configurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowCredentials(true);
                configuration.addAllowedOrigin("http://localhost:3000");
                configuration.addAllowedHeader("*");
                configuration.addAllowedMethod("*");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

}
