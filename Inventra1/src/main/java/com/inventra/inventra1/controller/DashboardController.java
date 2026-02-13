package com.inventra.inventra1.controller;

import com.inventra.inventra1.model.User;
import com.inventra.inventra1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    // Main entry point: Spring Security redirects here after a successful login
    @GetMapping("/dashboard")
    public String redirectByRole(Authentication authentication) {
        // Logic to route users based on their assigned role
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            return "redirect:/manager/dashboard";
        } else {
            return "redirect:/staff/dashboard";
        }
    }

    /**
     * Helper method to populate the UI model with user-specific data.
     * This prevents the "Property name cannot be found on null" error.
     */
    private void setupDashboardData(Model model, Authentication authentication, String roleName, String featureList) {
        // authentication.getName() returns the email used during login
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        model.addAttribute("user", user); // Crucial for ${user.username} in HTML
        model.addAttribute("role", roleName);
        model.addAttribute("features", featureList);
    }

    // 1. Admin Dashboard View
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        setupDashboardData(model, authentication, "Admin",
                "Full user management, system-wide statistics, and complete inventory visibility");
        return "dashboard"; // Points to dashboard.html
    }

    // 2. Manager Dashboard View
    @GetMapping("/manager/dashboard")
    public String managerDashboard(Authentication authentication, Model model) {
        setupDashboardData(model, authentication, "Manager",
                "Inventory management and detailed reporting tools");
        return "dashboard";
    }

    // 3. Staff Dashboard View
    @GetMapping("/staff/dashboard")
    public String staffDashboard(Authentication authentication, Model model) {
        setupDashboardData(model, authentication, "Staff",
                "View products, update stock levels, and monitor low-stock warnings");
        return "dashboard";
    }
}