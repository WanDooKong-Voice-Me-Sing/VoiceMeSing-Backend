package com.wandookong.voice_me_sing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin")
    public String adminProcess() {
        return "admin controller";
    }
}
