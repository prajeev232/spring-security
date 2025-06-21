package com.prajeev.secureweb.services;

import com.prajeev.secureweb.domain.Authority;
import com.prajeev.secureweb.domain.User;
import com.prajeev.secureweb.domain.UserProfile;
import com.prajeev.secureweb.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManagementService {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(String username, String password, BigDecimal salary, String... roles) {
        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        Set<Authority> authorities = Arrays.stream(roles).map(role -> {
            Authority authority = new Authority();
            authority.setAuthority(role);
            authority.setUser(user);
            return authority;
        }).collect(Collectors.toSet());

        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setSalary(salary);

        user.setProfile(userProfile);
        user.setAuthorities(authorities);

        UserDetails userDetails = new CustomUserDetails(user);

        userDetailsManager.createUser(userDetails);
    }

    public void deleteUser(String username) {
        userDetailsManager.deleteUser(username);
    }

    public void changePassword(String oldPassword, String newPassword) {
        userDetailsManager.changePassword(oldPassword,
                passwordEncoder.encode(newPassword));
    }
}
