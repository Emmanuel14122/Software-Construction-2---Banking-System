package app.infrastructure.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/system-users/internal_analyst/users").permitAll() // ← agrega esta
            // Login público
            //windowEmployee
            .requestMatchers("/system-users/window_employe/**").hasRole("WindowEmployee")
            //salesEmployee
            .requestMatchers("/system-users/sales_employe/**").hasRole("SalesEmployee")
            //personCustomerUser
            .requestMatchers("/system-users/person_customer_user/**").hasRole("PersonCustomerUser")
            //corporateCustomerUser
            .requestMatchers("/system-users/corporate_customer_user/**").hasRole("CorporateCustomerUser")
            //corporateEmployee
            .requestMatchers("/system-users/corporate_employe/**").hasRole("CorporateEmployee")
            //corporateSupervisor
            .requestMatchers("/system-users/corporate_supervisor/**").hasRole("CorporateSupervisor")
            //internalAnalyst
            .requestMatchers("/system-users/internal_analyst/**").hasRole("InternalAnalyst")
            //permite ver los endpoints de Swagger sin autenticación
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"status\":401,\"message\":\"No autenticado: se requiere un token válido\",\"errors\":null}");
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"status\":403,\"message\":\"Acceso denegado: no tiene permisos para este recurso\",\"errors\":null}");
                    })
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
