package com.wandookong.voice_me_sing.controller_test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class GuestController {

    @GetMapping("/guest")
    public String guestC() {
        return "guest controller";
    }
}
