package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "")
@Getter
@Setter
@ToString
public class CreateSongDTO {

    @Schema(description = "", example = "")
    private String resultSongName;

    @Schema(description = "", example = "")
    private MultipartFile songFile;

    @Schema(description = "", example = "")
    private Long voiceModelId;
}
