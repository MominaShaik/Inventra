package com.inventra.inventra1.service;

import com.inventra.inventra1.model.User;
import com.inventra.inventra1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Step 1: Find the user in MySQL using the email from the login form
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Step 2: Convert your User entity into a Spring Security UserDetails object
        // Note: Spring Security adds "ROLE_" prefix automatically to the role string
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole()) // e.g., "ADMIN" becomes "ROLE_ADMIN"
                .build();              // Fixed the missing build call
    }
}