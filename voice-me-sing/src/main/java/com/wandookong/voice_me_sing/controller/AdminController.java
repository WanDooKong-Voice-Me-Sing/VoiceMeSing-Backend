package com.wandookong.voice_me_sing.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Administrator API", description = "administrator page (reserved for future use)\n관리자 API")
public class AdminController {

    @GetMapping("/admin")
    public String adminProcess() {
        return "admin page";
    }
}