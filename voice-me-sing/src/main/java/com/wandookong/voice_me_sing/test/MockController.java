package com.wandookong.voice_me_sing.test;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Test API", description = "사용하지 마세요")
public class MockController {

    private final MockService mockService;

    @PostMapping(value = "/test", consumes = "multipart/form-data")
    public void uploadMockSong(
            @Parameter(description = "Audio file to be transformed\n변환할 오디오 파일", required = true, example = "cover_song.mp3", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(name = "songFile") MultipartFile songFile) throws IOException {

        // Mock 삽입 프로세스
        mockService.saveMockFile(songFile);
    }
}