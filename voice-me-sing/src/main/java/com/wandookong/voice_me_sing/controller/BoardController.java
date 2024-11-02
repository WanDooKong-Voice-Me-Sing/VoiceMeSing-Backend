package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.BoardDTO;
import com.wandookong.voice_me_sing.dto.BoardSaveDTO;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/write")
    public ResponseEntity<?> save(
            @RequestHeader(value = "access") String accessToken,
            @RequestBody BoardSaveDTO boardSaveDTO) {

        String msg = boardService.save(boardSaveDTO, accessToken);
        if ("SAVED".equals(msg)) {
            ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>("success", "board uploaded",
                    Map.of(
                            "boardTitle", boardSaveDTO.getBoardTitle(),
                            "boardContents", boardSaveDTO.getBoardContents()
                    ));
            return ResponseEntity.ok().body(responseDTO);
        } else {
            ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>("fail", "board upload failed", null);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> findAll() {

        List<BoardDTO> boardDTOList = boardService.findAll();

        ResponseDTO<List<BoardDTO>> responseDTO = new ResponseDTO<>("success", "get board list successfully", boardDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }
}
