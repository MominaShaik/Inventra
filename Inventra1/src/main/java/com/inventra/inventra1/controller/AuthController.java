package com.inventra.inventra1.controller;

import com.inventra.inventra1.model.User;
import com.inventra.inventra1.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // 1. SIGN-UP FLOW
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        // Prepare a blank User object for the Thymeleaf form
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") User user, Model model) {
        // Pass the user entity to the service for encryption and saving
        String result = authService.registerUser(user);

        if ("Signup successful".equals(result)) {
            // Redirect to login page with a success parameter
            return "redirect:/login?success";
        } else {
            // Stay on signup page and display the error message
            model.addAttribute("error", result);
            return "signup";
        }
    }

    // 2. LOGIN PAGE DISPLAY
    @GetMapping("/login")
    public String showLoginPage() {
        // Simply returns the login.html view
        return "login";
    }

    /* NOTE: The @PostMapping("/signin") has been REMOVED.
       Spring Security's filter chain now intercepts the POST request to /signin
       automatically using the CustomUserDetailsService you provided.
    */
}