package com.wandookong.voice_me_sing.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

    @GetMapping("/")
    public String mainC() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return "main controller " + username;
    }

}
