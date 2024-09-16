package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.oauth2.CookieUtil;
import com.wandookong.voice_me_sing.service.ReissueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Token Reissue API", description = "Reissues access and refresh tokens\naccess 토큰과 refresh 토큰 재발급")
public class ReissueController {

    private final ReissueService reissueService;
    private final CookieUtil cookieUtil;

    @Operation(
            summary = "Reissue access and refresh tokens\naccess 토큰과 refresh 토큰 재발급",
            description = "Reissue access and refresh tokens if a valid refresh token is provided.\n유효한 refresh 토큰이 제공되면 access 토큰과 refresh 토큰을 재발급")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access and refresh tokens reissued\naccess 토큰과 refresh 토큰 재발급",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"access and refresh tokens reissued\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400", description = "Invalid refresh token\n유효하지 않은 refresh 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"invalid refresh token\",\"data\":null}")
                    )
            )
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Parameter(description = "refresh token", required = true)
                                         @RequestHeader(value = "refresh", required = false) String authHeader, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        // get refresh token
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        System.out.println("refreshToken = " + refreshToken);

        Map<String, String> result = reissueService.reissueAccessRefresh(refreshToken);
        String newRefreshToken = result.get("newRefreshToken");
        String newAccessToken = result.get("newAccessToken");

        if (newRefreshToken == null || newAccessToken == null) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", result.get("message"), null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
        else {
            response.addCookie(cookieUtil.createCookie("refresh", newRefreshToken));
            response.setHeader("access", newAccessToken);

            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "access and refresh tokens reissued", null);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }

    }
}
