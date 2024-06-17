package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.JoinDTO;
import com.wandookong.voice_me_sing.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {

        joinService.joinProcess(joinDTO);

        return "join success";
    }
}
