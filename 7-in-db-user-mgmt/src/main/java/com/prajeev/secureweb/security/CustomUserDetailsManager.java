package com.prajeev.secureweb.security;

import com.prajeev.secureweb.domain.Authority;
import com.prajeev.secureweb.domain.User;
import com.prajeev.secureweb.domain.UserProfile;
import com.prajeev.secureweb.repositories.AuthorityRepository;
import com.prajeev.secureweb.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.math.BigDecimal;
import java.util.Optional;

public class CustomUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsManager(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createUser(UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User userToCreate = customUserDetails.getUser();

        User user = new User();

        user.setUsername(userToCreate.getUsername());
        user.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
        user.setEnabled(userToCreate.isEnabled());

        userDetails.getAuthorities().forEach(authority -> {
            Authority auth = new Authority();
            auth.setAuthority(authority.getAuthority());
            auth.setUser(user);
            user.getAuthorities().add(auth);
        });

        BigDecimal salary = extractSalaryOrThrow(userToCreate);
        UserProfile profile = new UserProfile();

        profile.setUser(user);
        profile.setSalary(salary);

        user.setProfile(profile);

        userRepository.save(user);
    }

    private BigDecimal extractSalaryOrThrow(User user) {
        return Optional.ofNullable(user.getProfile())
                .map(UserProfile::getSalary)
                .orElseThrow(() -> new IllegalArgumentException("A user's salary must be provided"));
    }

    @Override
    @Transactional
    public void updateUser(UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User userUpdate = customUserDetails.getUser();

        BigDecimal salaryUpdate = extractSalaryOrThrow(userUpdate);

        User existingUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        existingUser.setPassword(userDetails.getPassword());
        existingUser.setEnabled(userDetails.isEnabled());
        existingUser.getProfile().setSalary(salaryUpdate);

        authorityRepository.deleteAllByUserId(existingUser.getId());

        userDetails.getAuthorities().forEach(authority -> {
            Authority auth = new Authority();
            auth.setAuthority(authority.getAuthority());
            auth.setUser(existingUser);
            existingUser.getAuthorities().add(auth);
        });

        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (currentUser == null) {
            throw new IllegalStateException("No authentication found");
        }

        String username = currentUser.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), newPassword, currentUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println("User: " + user);
        System.out.println("Profile: " + user.getProfile());
        System.out.println("Authorities: " + user.getAuthorities());

        return new CustomUserDetails(user);
    }
}
