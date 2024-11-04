package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시글 삭제를 위한 데이터 전송 객체\nObject used for transferring data to delete a board post")
public class BoardDeleteDTO {

    @Schema(description = "삭제할 게시글의 식별자", example = "3")
    private String boardId;
}
