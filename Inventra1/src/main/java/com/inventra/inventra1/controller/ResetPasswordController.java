package com.inventra.inventra1.controller;

import com.inventra.inventra1.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ResetPasswordController {

    @Autowired
    private AuthService authService;

    // 1. Display the initial email entry page
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    // 2. Handle the "Send Reset Link" button click
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        // Service handles token generation and saving
        String result = authService.processForgotPassword(email);

        // REQUIREMENT: Always show a generic success message to the user
        if (result.startsWith("Password reset link generated")) {
            model.addAttribute("message", "A reset link is sent to registered mail");
        } else {
            // Display "Email not registered" or other error messages
            model.addAttribute("error", result);
        }

        return "forgot-password";
    }

    // 3. Display the page where user enters the new password
    // This is usually reached via a link in the email: /reset-password?token=XYZ
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    // 4. Handle the final password update
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {

        // Basic validation: Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("token", token);
            return "reset-password";
        }

        String result = authService.resetPassword(token, newPassword);

        if ("Password reset successful".equals(result)) {
            // Redirect to login with a special success flag
            return "redirect:/login?resetSuccess";
        } else {
            model.addAttribute("error", result);
            model.addAttribute("token", token);
            return "reset-password";
        }
    }
}