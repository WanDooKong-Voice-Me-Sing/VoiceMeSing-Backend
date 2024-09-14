package com.wandookong.voice_me_sing.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.SignupDTO;
import com.wandookong.voice_me_sing.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupProcess(@RequestBody SignupDTO signupDTO) {

        boolean signupSuccess = signupService.signup(signupDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(signupDTO, new TypeReference<Map<String, Object>>() {});
        ResponseDTO<Map<String, Object>> success = new ResponseDTO<>("success", "signup success", map);
        ResponseDTO<Map<String, Object>> fail = new ResponseDTO<>("fail", "signup fail", null);

        if (signupSuccess) {
            return ResponseEntity.status(HttpStatus.CREATED).body(success);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(fail);
        }

//        if (signupSuccess) return "signup success";
//        else return "signup failed";
    }
}
