package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "게시글 작성 시 필요한 제목과 내용을 담고 있는 데이터 전송 객체")
@Getter
@Setter
public class BoardSaveDTO {

    @Schema(description = "게시글 제목", example = "Sample Title")
    private String boardTitle;

    @Schema(description = "게시글 내용", example = "This is the content of the board post.")
    private String boardContents;
}
