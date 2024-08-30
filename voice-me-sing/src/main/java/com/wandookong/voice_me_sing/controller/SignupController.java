package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.SignupDTO;
import com.wandookong.voice_me_sing.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public String signupProcess(@RequestBody SignupDTO signupDTO) {

        boolean signupSuccess = signupService.signup(signupDTO);

        if (signupSuccess) return "signup success";
        else return "signup failed";
    }
}
