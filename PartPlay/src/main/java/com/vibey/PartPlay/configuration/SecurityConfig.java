package com.vibey.PartPlay.configuration;

import com.vibey.PartPlay.Security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * This filter forces Spring to eagerly write the XSRF-TOKEN cookie on
     * EVERY response — including the initial page load.
     *
     * By default, Spring Security 6 uses a "deferred" CSRF token strategy:
     * the cookie is only written when something in the application actually
     * reads the token (e.g. a Thymeleaf template rendering a hidden field).
     * Since our frontend is plain HTML with no server-side rendering, nothing
     * ever reads the token, so the cookie never gets written, and every
     * subsequent POST gets a 403.
     *
     * This filter reads the token attribute on every request, which triggers
     * Spring to write the cookie immediately.
     */
    @Bean
    public OncePerRequestFilter csrfCookieFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            jakarta.servlet.FilterChain filterChain)
                    throws jakarta.servlet.ServletException, IOException {
                // Reading the attribute is enough to trigger the cookie write
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) {
                    csrfToken.getToken(); // forces the cookie to be written in the response
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ── CSRF ──────────────────────────────────────────────────────────
                // CookieCsrfTokenRepository.withHttpOnlyFalse() writes XSRF-TOKEN
                // as a readable cookie so JavaScript can pick it up.
                // CsrfTokenRequestAttributeHandler stores the token as a request
                // attribute so our csrfCookieFilter above can eagerly load it.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/register")
                )

                // Apply the eager cookie-writing filter after the CSRF filter runs
                .addFilterAfter(csrfCookieFilter(),
                        org.springframework.security.web.csrf.CsrfFilter.class)

                // ── Session management ─────────────────────────────────────────────
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )

                // ── Route authorization ────────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login.html",
                                "/register.html",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers("/register", "/login").permitAll()
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