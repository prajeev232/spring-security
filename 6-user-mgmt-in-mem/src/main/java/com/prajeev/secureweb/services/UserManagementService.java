package com.prajeev.secureweb.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(String username, String password, String... roles) {
        UserDetails newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .build();

        userDetailsManager.createUser(newUser);
    }

    public void deleteUser(String username) {
        userDetailsManager.deleteUser(username);
    }

    public void changePassword(String oldPassword, String newPassword) {
        userDetailsManager.changePassword(oldPassword,
                passwordEncoder.encode(newPassword));
    }
}
