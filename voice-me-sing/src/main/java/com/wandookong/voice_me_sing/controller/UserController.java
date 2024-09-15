package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("access") String accessToken) {

//        String accessToken = request.getHeader("access");
//        String email = jwtUtil.getEmail(accessToken);

        String nickname = userService.getProfile(accessToken);

        if (nickname == null) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "no user found", null);
            return ResponseEntity.status(401).body(responseDTO);
        }
        else {
            ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "get profile",
                    Map.of("nickname", nickname));
            return ResponseEntity.status(200).body(responseDTO);
        }
    }
}
