package com.inventra.inventra1.repository;

import com.inventra.inventra1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during Signup to check if a username is already taken
    Optional<User> findByUsername(String username);

    // Primary method for Sign-in and Forgot Password (using email as the unique ID)
    Optional<User> findByEmail(String email);

    // Crucial for the second step of the password reset flow
    Optional<User> findByResetToken(String resetToken);
}