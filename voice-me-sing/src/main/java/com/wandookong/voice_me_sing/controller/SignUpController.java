package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.SignUpDTO;
import com.wandookong.voice_me_sing.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping("/signup")
    public String signUpC(SignUpDTO signUpDTO) {

        signUpService.signUpProcess(signUpDTO);

        return "sign up success";
    }
}
