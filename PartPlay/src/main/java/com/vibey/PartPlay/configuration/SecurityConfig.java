package com.vibey.PartPlay.configuration;

import com.vibey.PartPlay.Security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Spring Security 6.x: pass UserDetailsService via constructor
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ── CSRF ──────────────────────────────────────────────────────────
                // Enabled for all browser requests.
                // /register and /login are excluded — no session/token exists yet at that point.
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/register", "/login")
                )

                // ── Session management ─────────────────────────────────────────────
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                )

                // ── Route authorization ────────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        // Only login + register pages and static assets are public
                        .requestMatchers(
                                "/login.html",
                                "/register.html",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // Public API endpoints (registration + login processing)
                        .requestMatchers("/register", "/login").permitAll()
                        // Everything else — index.html, profile.html, /users/** — requires login
                        .anyRequest().authenticated()
                )

                // ── Form login ─────────────────────────────────────────────────────
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/index.html", true)
                        .failureUrl("/login.html?error=true")
                        .permitAll()
                )

                // ── Logout ─────────────────────────────────────────────────────────
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}