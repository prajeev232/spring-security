package com.prajeev.secureweb.controller;

import com.prajeev.secureweb.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping(path = "/")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        model.addAttribute("creditCardNumber", "1234-3538-2329-1293");
        model.addAttribute("salary", customUserDetails.getUser().getProfile().getSalary());

        return "dashboard";
    }
}
