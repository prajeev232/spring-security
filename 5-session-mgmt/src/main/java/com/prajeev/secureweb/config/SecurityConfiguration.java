package com.prajeev.secureweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
//                Specific path and HTTP method matching
                .requestMatchers(HttpMethod.GET, "/api/public-data").permitAll()
//                Using REGEX for OPTIONS endpoint
                .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.OPTIONS, "^/api/public.*$")).permitAll()
//                Ant style pattern matching
                .requestMatchers("/api/private**").authenticated()
                        .anyRequest().authenticated()
        ).formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/").permitAll())
                .logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
                .httpBasic(basic -> {})
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                        .expiredUrl("/login?logout"))
                .rememberMe(remember -> remember
                        .key("secret-key")
                        .tokenValiditySeconds(86400));

        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        This is the default setup for session management.
////        http.sessionManagement(session ->
////                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
////                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId));
//
//        http.sessionManagement(session -> session
//                .maximumSessions(1)
////              Strat 1: Prevent new logins
//                        .maxSessionsPreventsLogin(true)
////                Strat 2: Expire existing session
////                        .maxSessionsPreventsLogin(false)
//                        .sessionRegistry(sessionRegistry())
//                        .expiredUrl("/login?logout")
//        );
//
//        return http.build();
//    }

// Remember me implementation -> uses either a token-based implementation (less secure) or a persistent token approach (more secure)

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.rememberMe(remember -> remember
//                .key("uniqueAndSecretKey")
//                .tokenValiditySeconds(86400))
//                .build();
//
//        return http.build();
//    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
