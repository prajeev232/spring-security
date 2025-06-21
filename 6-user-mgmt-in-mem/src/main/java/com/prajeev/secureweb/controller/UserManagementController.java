package com.prajeev.secureweb.controller;

import com.prajeev.secureweb.services.UserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {
    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public String showUserManagementPage() {
        return "user-mgmt";
    }

    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam(required = false) String[] roles,
                             RedirectAttributes redirectAttributes) {
        try {
            String[] finalRoles = roles != null ? roles : new String[]{"USER"};
            userManagementService.createUser(username, password, finalRoles);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
        }

        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String username,
                             RedirectAttributes redirectAttributes) {
        try {
            userManagementService.deleteUser(username);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        }

        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            userManagementService.changePassword(oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
        }

        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }
}
