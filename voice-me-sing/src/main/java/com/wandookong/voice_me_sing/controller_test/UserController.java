package com.wandookong.voice_me_sing.controller_test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class UserController {

    @GetMapping("/user")
    public String userC() {
        return "user controller";
    }
}
