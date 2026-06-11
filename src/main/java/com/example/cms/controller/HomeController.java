package com.example.cms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 첫 화면.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";   // templates/index.html
    }
}
