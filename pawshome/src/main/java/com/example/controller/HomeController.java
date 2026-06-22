package com.example.controller;

import com.example.service.DbConnectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DbConnectionService dbConnectionService;

    public HomeController(DbConnectionService dbConnectionService) {
        this.dbConnectionService = dbConnectionService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("connected", dbConnectionService.isConnected());
        model.addAttribute("dbStatus", dbConnectionService.getStatus());
        return "index";
    }
}
