package tech.challenge.scheduling.system.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        private final AccessTokenFilter accessTokenFilter;
        private final UserDetailsService userDetailsService;

        public SecurityConfig(AccessTokenFilter accessTokenFilter, UserDetailsService userDetailsService) {
                this.accessTokenFilter = accessTokenFilter;
                this.userDetailsService = userDetailsService;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                        .authorizeHttpRequests(req -> {
                            req.requestMatchers(HttpMethod.POST, "/users").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/admins").permitAll();
                            req.requestMatchers("/auth/login", "/auth").permitAll();
                            req.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                    "/swagger-resources/**", "/webjars/**").permitAll();

                            // PACIENTES
                            req.requestMatchers(HttpMethod.GET, "/patients/me").hasRole("PATIENT");
                            req.requestMatchers(HttpMethod.POST, "/patients").hasAnyRole("ADMIN", "DOCTOR", "NURSE");
                            req.requestMatchers(HttpMethod.PUT, "/patients/{id}").hasAnyRole("ADMIN", "DOCTOR", "NURSE");
                            req.requestMatchers(HttpMethod.DELETE, "/patients/{id}").hasRole("ADMIN");

                            // MÉDICOS
                            req.requestMatchers(HttpMethod.GET, "/doctors/me").hasRole("DOCTOR");
                            req.requestMatchers(HttpMethod.POST, "/doctors").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/doctors/{id}").hasAnyRole("ADMIN", "DOCTOR");
                            req.requestMatchers(HttpMethod.DELETE, "/doctors/{id}").hasRole("ADMIN");

                            // ENFERMEIROS
                            req.requestMatchers(HttpMethod.GET, "/nurses/me").hasRole("NURSE");
                            req.requestMatchers(HttpMethod.POST, "/nurses").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/nurses/{id}").hasAnyRole("ADMIN", "NURSE");
                            req.requestMatchers(HttpMethod.DELETE, "/nurses/{id}").hasRole("ADMIN");

                            req.anyRequest().authenticated();
                        })
                        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .csrf(csrf -> csrf.disable())
                        .addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class)
                        .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }
}