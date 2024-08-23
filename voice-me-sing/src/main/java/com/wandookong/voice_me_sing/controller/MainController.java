package com.wandookong.voice_me_sing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String mainProcess() {

//        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return "main controller";
    }

}
