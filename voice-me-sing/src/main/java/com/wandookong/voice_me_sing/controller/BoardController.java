package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.BoardDTO;
import com.wandookong.voice_me_sing.dto.BoardSaveDTO;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Save a new board post\n새로운 게시판 글 작성",
            description = "Allows a user to create a new board post with the given title and contents, authenticated by the access token.\n액세스 토큰을 사용하여 주어진 제목과 내용으로 새로운 게시판 글 작성"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully uploaded board post\n게시판 글을 성공적으로 업로드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"board uploaded\",\"data\":" +
                                    "{\"boardTitle\":\"Sample Title\",\"boardContents\":\"This is the content of the board post.\"}}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Board upload failed\n게시판 글 업로드 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"board upload failed\",\"data\":null}")
                    )
            )
    })
    @PostMapping("/write")
    public ResponseEntity<?> save(
            @Parameter(description = "Access token for authentication\n인증을 위한 access 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "게시글 작성에 필요한 정보\n게시글 제목과 내용을 포함하는 객체", required = true)
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

    @Operation(
            summary = "Retrieve all boards\n모든 게시판 리스트 조회",
            description = "Fetches the complete list of boards from the server.\n서버에서 모든 게시판 리스트를 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the board list\n게시판 리스트를 성공적으로 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get board list successfully\",\"data\":" +
                                    "[{\"boardId\":1,\"boardTitle\":\"Announcements\",\"boardWriter\":\"nickname\",\"boardContents\":\"Welcome to the announcements board!\",\"boardHits\":10,\"boardCreatedTime\":\"2024-11-01T10:00:00\",\"boardUpdatedTime\":\"2024-11-01T10:00:00\"}," +
                                    "{\"boardId\":2,\"boardTitle\":\"General Discussion\",\"boardWriter\":\"kyungbin\",\"boardContents\":\"Feel free to discuss any topic here!\",\"boardHits\":5,\"boardCreatedTime\":\"2024-11-01T11:00:00\",\"boardUpdatedTime\":\"2024-11-01T11:00:00\"}," +
                                    "{\"boardId\":3,\"boardTitle\":\"Feedback\",\"boardWriter\":\"nickname2\",\"boardContents\":\"Share your feedback about our service!\",\"boardHits\":2,\"boardCreatedTime\":\"2024-11-01T12:00:00\",\"boardUpdatedTime\":\"2024-11-01T12:00:00\"}]}"
                            )
                    )
            )
    })
    @GetMapping("/")
    public ResponseEntity<?> findAll() {

        List<BoardDTO> boardDTOList = boardService.findAll();

        ResponseDTO<List<BoardDTO>> responseDTO = new ResponseDTO<>("success", "get board list successfully", boardDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }
}
