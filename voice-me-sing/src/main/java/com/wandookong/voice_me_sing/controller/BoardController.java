package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.*;
import com.wandookong.voice_me_sing.service.BoardService;
import com.wandookong.voice_me_sing.service.UserService;
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
    private final UserService userService;

    @Operation(
            summary = "Write a new post\n새 게시글 작성",
            description = "Allows a user to write a new post. Uses access token to authenticate the user and retrieve their nickname for the post.\n사용자가 새로운 게시글을 작성할 수 있도록 하며, 액세스 토큰을 사용하여 사용자 인증 및 게시글 작성자의 닉네임을 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully uploaded the post\n게시글을 성공적으로 업로드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"board uploaded\",\"data\":{\"boardId\":1,\"boardTitle\":\"New Post\",\"boardWriter\":\"User1\",\"boardContents\":\"This is a new post content.\",\"boardHits\":0,\"boardCreatedTime\":\"2024-11-01T10:00:00\",\"boardUpdatedTime\":\"2024-11-01T10:00:00\"}}")
                    )
            )
    })
    @PostMapping("/write")
    // 게시글 작성
    public ResponseEntity<?> save(
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "게시글 작성에 필요한 정보\n게시글 제목과 내용을 포함하는 객체", required = true)
            @RequestBody BoardSaveDTO boardSaveDTO) {

        // 게시글 저장 프로세스
        BoardDTO boardDTO = boardService.save(boardSaveDTO, accessToken);

        // 응답 생성
        ResponseDTO<BoardDTO> responseDTO = new ResponseDTO<>("success", "board uploaded", boardDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(
            summary = "Get all posts\n전체 게시글 조회",
            description = "Fetches the list of all posts available in the system.\n시스템에 있는 모든 게시글 목록을 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved board list\n게시글 목록을 성공적으로 조회",
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
    // 전체 게시글 조회
    public ResponseEntity<?> findAll() {
        // 전체 게시글 조회 프로세스
        List<BoardDTO> boardDTOList = boardService.findAll();

        // 응답 생성
        ResponseDTO<List<BoardDTO>> responseDTO = new ResponseDTO<>("success", "get board list successfully", boardDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(
            summary = "Get current user's posts\n현재 사용자의 게시글 조회",
            description = "Fetches the list of posts created by the authenticated user.\n인증된 사용자가 작성한 게시글 목록을 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's post list\n사용자의 게시글 목록을 성공적으로 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get user's post list successfully\",\"data\":[{\"boardId\":1,\"boardTitle\":\"My Post Title\",\"boardWriter\":\"UserNickname\",\"boardContents\":\"Content of my post.\",\"boardHits\":15,\"boardCreatedTime\":\"2024-11-01T10:00:00\",\"boardUpdatedTime\":\"2024-11-01T10:05:00\"}]}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Failed to retrieve user's post list\n사용자의 게시글 목록 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"failed to get user's post list\",\"data\":null}")
                    )
            )
    })
    @GetMapping("/my-post")
    // 내 게시글 조회
    public ResponseEntity<?> findMyPost(
            @Parameter(description = "Access token for user authentication\n사용자 인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken) {

        // 엑세스 토큰으로부터 nickname 추출 후 해당 nickname으로 작성된 게시글 검색
        String nickname = userService.getNicknameByToken(accessToken);
        List<BoardDTO> boardDTOList = boardService.findByNickname(nickname);

        // 응답 생성
        ResponseDTO<List<BoardDTO>> responseDTO = new ResponseDTO<>("success", "get user's post list successfully", boardDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(
            summary = "Get posts by user's nickname\n사용자의 닉네임으로 게시글 조회",
            description = "Fetches posts by a given user's nickname. If the requested nickname matches the authenticated user's nickname, it redirects to retrieving the user's own posts.\n주어진 사용자의 닉네임으로 게시글을 조회. 요청된 닉네임이 인증된 사용자와 같다면 자신의 게시글을 조회하도록 리다이렉트"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's post list\n사용자의 게시글 목록을 성공적으로 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get user's post list successfully\",\"data\":[{\"boardId\":1,\"boardTitle\":\"Post Title\",\"boardWriter\":\"UserNickname\",\"boardContents\":\"Content of the post.\",\"boardHits\":10,\"boardCreatedTime\":\"2024-11-01T10:00:00\",\"boardUpdatedTime\":\"2024-11-01T10:05:00\"}]}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Failed to retrieve user's post list\n사용자의 게시글 목록 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"failed to get user's post list\",\"data\":null}")
                    )
            )
    })
    @GetMapping("/post-{nickname}")
    // {nickname} 으로 작성된 게시글 조회
    public ResponseEntity<?> findUserPost(
            @Parameter(description = "Access token for user authentication\n사용자 인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "Nickname of the user whose posts are to be retrieved\n조회할 사용자의 닉네임", required = true)
            @PathVariable(value = "nickname") String nickname
    ) {
        // 1. 내 게시글을 찾는다면(내 닉네임 = 찾고자 하는 닉네임) /my-post findMyPost()로 리다이렉트
        // 엑세스 토큰으로부터 nickname 추출 (내 닉네임 조회)
        String userNickname = userService.getNicknameByToken(accessToken);
        // 조회하고자 하는 닉네임과 같다면 리다이렉트
        if (userNickname.equals(nickname)) {
            return findMyPost(accessToken);
        }

        // 2. 해당 닉네임으로 작성된 게시글 리스트 조회
        List<BoardDTO> boardDTOList = boardService.findByNickname(nickname);

        // 응답 생성
        ResponseDTO<List<BoardDTO>> responseDTO = new ResponseDTO<>("success", "get user's post list successfully", boardDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }


    @Operation(
            summary = "Retrieve a specific post by ID\nID로 특정 게시글 조회",
            description = "Fetches the post details by its ID. Updates the view count and checks if the user is the original writer.\n게시글 ID로 게시글 세부 정보를 조회. 조회수를 업데이트하고 사용자가 원래 작성자인지 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the post\n게시글 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get the post successfully\",\"data\":{\"board\":{\"boardId\":1,\"boardTitle\":\"Sample Title\",\"boardWriter\":\"User1\",\"boardContents\":\"Content of the post.\",\"boardHits\":10,\"boardCreatedTime\":\"2024-11-01T10:00:00\",\"boardUpdatedTime\":\"2024-11-01T10:05:00\"},\"isWriter\":true}}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Failed to get the post\n게시글 조회 실패 (해당 게시글이 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"failed to get the post\",\"data\":null}")
                    )
            )
    })
    @GetMapping("/{id}")
    // 게시글 아이디로 조회
    public ResponseEntity<?> findById(
            @Parameter(description = "Access token for user authentication. Optional if the user just wants to view the post.\n사용자 인증을 위한 엑세스 토큰. 사용자가 게시글을 보기만 할 경우 선택적", required = false)
            @RequestHeader(value = "access", required = false) String accessToken,

            @Parameter(description = "ID of the post to retrieve\n조회할 게시글의 ID", required = true)
            @PathVariable(value = "id") String boardId) {

        // 1. 해당 게시글 조회 (해당 게시글 없을 경우 null)
        BoardDTO boardDTO = boardService.findById(boardId);
        // 2. 작성자 확인 (수정 여부 표시 위함)
        boolean isWriter = false;
        // 게시글이 존재하고, 로그인한 상태라면(= accessToken 존재) 작성자 확인
        if (boardDTO != null && accessToken != null) {
            isWriter = boardService.checkWriter(boardId, accessToken);
        }

        // 응답 생성
        if (boardDTO == null) {
            ResponseDTO<BoardDTO> responseDTO = new ResponseDTO<>("fail", "failed to get the post", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        } else {
            ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "get the post successfully",
                    Map.of(
                            "board", boardDTO,
                            "isWriter", isWriter
                    ));
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @Operation(
            summary = "Delete a user's post\n사용자의 게시글 삭제",
            description = "Allows a user to delete their own post by providing a board ID and an access token for authentication. Verifies that the user requesting the deletion is the original writer.\n사용자가 자신의 게시글을 삭제할 수 있도록 하며, 삭제 요청자가 원래 작성자인지 확인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the post\n게시글을 성공적으로 삭제",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"delete the post successfully\",\"data\":null}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Failed to delete the post\n게시글 삭제 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"failed delete the post\",\"data\":null}")
                    )
            )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(
            @Parameter(description = "Access token for user authentication\n사용자 인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "ID of the board to be deleted\n삭제할 게시글의 ID", required = true)
            @RequestBody BoardDeleteDTO boardDeleteDTO) {

        // 게시글 아이디 추출 후 삭제 프로세스
        String boardId = boardDeleteDTO.getBoardId();
        boolean deleted = boardService.delete(boardId, accessToken);

        // 응답 생성
        if (deleted) {
            ResponseDTO<List<BoardDTO>> responseDTO = new ResponseDTO<>("success", "delete the post successfully", null);
            return ResponseEntity.ok().body(responseDTO);
        } else {
            ResponseDTO<List<BoardDTO>> responseDTO = new ResponseDTO<>("success", "failed delete the post", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @Operation(
            summary = "Edit a user's post\n사용자의 게시글 수정",
            description = "Allows a user to edit their own post. Checks if the user is the original writer before editing.\n사용자가 자신의 게시글을 수정할 수 있게 하며, 수정 요청자가 원래 작성자인지 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully edited the post\n게시글 수정을 성공적으로 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"edit user's post successfully\",\"data\":{\"boardId\":1,\"boardTitle\":\"Updated Title\",\"boardWriter\":\"User1\",\"boardContents\":\"Updated content of the post.\",\"boardHits\":10,\"boardCreatedTime\":\"2024-11-01T10:00:00\",\"boardUpdatedTime\":\"2024-11-01T10:05:00\"}}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Failed to edit the post\n게시글 수정 실패 (게시글 작성자와 수정 요청자가 다름)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"failed to edit the post\",\"data\":null}")
                    )
            )
    })
    @PatchMapping("/edit")
    // 게시글 수정
    public ResponseEntity<?> editPost(
            @Parameter(description = "Access token for user authentication\n사용자 인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "Data required to edit the post, including board ID, new title, and new contents\n게시글 수정을 위한 데이터, 게시글 ID, 새로운 제목 및 내용 포함", required = true)
            @RequestBody BoardEditDTO boardEditDTO) {

        // 게시글 수정 프로세스
        BoardDTO boardDTO = boardService.editPost(accessToken, boardEditDTO);

        // 응답 생성
        if (boardDTO != null) {
            ResponseDTO<BoardDTO> responseDTO = new ResponseDTO<>("success", "edit user's post successfully", boardDTO);
            return ResponseEntity.ok().body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "failed to edit the post", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }
}
