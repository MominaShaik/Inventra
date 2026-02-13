package com.inventra.inventra1.service;

import com.inventra.inventra1.model.User;
import com.inventra.inventra1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender; // Added to handle actual email delivery

    // 1. SIGN-UP LOGIC
    public String registerUser(User userDetails) {
        if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            return "Email already registered";
        }
        if (userRepository.findByUsername(userDetails.getUsername()).isPresent()) {
            return "Username already exists";
        }

        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userRepository.save(userDetails);
        return "Signup successful";
    }

    // 2. FORGOT PASSWORD LOGIC (Rectified for actual Mail Sending)
    public String processForgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Email not registered";
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // --- ACTUAL MAIL SENDING LOGIC ---
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com"); // Matches your application.properties
            message.setTo(email);
            message.setSubject("Inventra - Password Reset Request");
            message.setText("Hello " + user.getUsername() + ",\n\n" +
                    "To reset your password, please click the link below:\n" +
                    "http://localhost:8080/reset-password?token=" + token + "\n\n" +
                    "This link will expire in 15 minutes.");

            mailSender.send(message);
            return "Password reset link generated: " + token;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send email. Please check your connection.";
        }
    }

    // 3. RESET PASSWORD LOGIC
    public String resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);

        if (userOpt.isEmpty() || userOpt.get().getTokenExpiry().isBefore(LocalDateTime.now())) {
            return "Invalid or expired token";
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        return "Password reset successful";
    }
}