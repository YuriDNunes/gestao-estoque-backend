package com.management.inventory.config;

import com.management.inventory.security.jwt.JwtTokenFilter;
import com.management.inventory.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private JwtTokenProvider tokenProvider;

    public SecurityConfig(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        JwtTokenFilter filter = new JwtTokenFilter(tokenProvider);
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //Autoriza quais endpoints tem permissão, quais precisam autenticar e quais são negados
                .authorizeHttpRequests(
                    authorizeHttpRequests -> authorizeHttpRequests
                            .requestMatchers(
                                    "/auth/signin",
                                    "/auth/refresh/**"
                            ).permitAll()
                            .requestMatchers("/api/user/**").hasAnyAuthority("ROLE_Admin", "ROLE_Gestor")
                            .requestMatchers("api/product/**", "api/history/").hasAnyAuthority("ROLE_Admin", "ROLE_Gestor", "ROLE_Usuário")
                            .requestMatchers("/api/**").authenticated()
                            .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .build();
    }
}
