package tn.enicarthage.gestionformation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import tn.enicarthage.gestionformation.security.CustomUserDetailsService;
import tn.enicarthage.gestionformation.security.RateLimitingFilter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RateLimitingFilter rateLimitingFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, RateLimitingFilter rateLimitingFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/inscription", "/inscription/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/formateurs/nouveau", "/formateurs/enregistrer", "/formateurs/supprimer/**").hasRole("ADMIN")
                        .requestMatchers("/formateurs/**").hasAnyRole("ADMIN", "FORMATEUR")
                        .requestMatchers("/cycles/nouveau", "/cycles/enregistrer", "/cycles/modifier/**", "/cycles/supprimer/**").hasRole("ADMIN")
                        .requestMatchers("/cycles/**").hasAnyRole("ADMIN", "FORMATEUR")
                        .requestMatchers("/participants/supprimer/**").hasRole("ADMIN")
                        .requestMatchers("/participants/**").hasAnyRole("ADMIN", "PARTICIPANT")
                        .requestMatchers("/chatbot/**").hasRole("PARTICIPANT")
                        .requestMatchers("/salles/**").hasRole("ADMIN")
                        .requestMatchers("/formateurs/mes-cycles/**").hasAnyRole("ADMIN", "FORMATEUR")
                        .requestMatchers("/mes-absences/fichier/**").hasAnyRole("ADMIN", "PARTICIPANT")
                        .requestMatchers("/mes-absences/**").hasRole("PARTICIPANT")
                        .requestMatchers("/mes-formations/**").hasRole("PARTICIPANT")
                        .requestMatchers("/mes-formations/evaluer/**").hasRole("PARTICIPANT")
                        .requestMatchers("/chatbot/generer-description").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(contentType -> {})
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                                        "font-src 'self' https://fonts.gstatic.com; " +
                                        "img-src 'self' data:; " +
                                        "script-src 'self' https://cdnjs.cloudflare.com"
                        ))
                        .referrerPolicy(referrer -> referrer.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                        ))
                )
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/menu", true)
                        .failureHandler(authenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                        .expiredUrl("/login?erreur=session_expiree")
                );

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String erreur = (exception instanceof DisabledException) ? "compte_inactif" : "identifiants";
            response.sendRedirect("/login?erreur=" + erreur);
        };
    }
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}