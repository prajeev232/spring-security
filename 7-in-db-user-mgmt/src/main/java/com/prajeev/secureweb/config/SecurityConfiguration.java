package com.prajeev.secureweb.config;

import com.prajeev.secureweb.domain.Authority;
import com.prajeev.secureweb.domain.User;
import com.prajeev.secureweb.domain.UserProfile;
import com.prajeev.secureweb.repositories.AuthorityRepository;
import com.prajeev.secureweb.repositories.UserProfileRepository;
import com.prajeev.secureweb.repositories.UserRepository;
import com.prajeev.secureweb.security.CustomUserDetails;
import com.prajeev.secureweb.security.CustomUserDetailsManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/private**").authenticated()
                        .anyRequest().authenticated()
        ).formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/").permitAll())
                .logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
                .httpBasic(basic -> {});

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsService(UserRepository userRepository, UserProfileRepository userProfileRepository, AuthorityRepository authorityRepository) {
        CustomUserDetailsManager userDetailsManager = new CustomUserDetailsManager(userRepository, authorityRepository, passwordEncoder());

        if (!userDetailsManager.userExists("user")) {
            User customUser = new User();

            Authority authority = new Authority();
            authority.setAuthority("ADMIN");
            authority.setUser(customUser);
            Set<Authority> authorities = new HashSet<>();
            authorities.add(authority);

            UserProfile userProfile = new UserProfile();
            userProfile.setSalary(new BigDecimal(9999));
            userProfile.setUser(customUser);

            customUser.setUsername("user");
            customUser.setPassword("password");
            customUser.setAuthorities(authorities);
            customUser.setProfile(userProfile);

            UserDetails customUserDetails = new CustomUserDetails(customUser);

            userDetailsManager.createUser(customUserDetails);
        }

        return userDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
