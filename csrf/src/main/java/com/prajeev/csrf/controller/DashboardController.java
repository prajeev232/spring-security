package com.prajeev.csrf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping(path = "/")
    public String dashboard(Model model) {
        model.addAttribute("creditCardNumber", "1234-3538-2329-1293");
        model.addAttribute("salary", "$999");

        return "dashboard";
    }
}
