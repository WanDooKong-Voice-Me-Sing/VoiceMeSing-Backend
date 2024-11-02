package com.wandookong.voice_me_sing.dto;

import com.wandookong.voice_me_sing.entity.BoardEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "게시판 게시글의 정보를 담고 있는 데이터 전송 객체, 사용자에게 게시글 리스트 제공 시 사용")
@Getter
@Setter
public class BoardDTO {

    @Schema(description = "게시글의 식별자", example = "1")
    private Long boardId;

    @Schema(description = "게시글의 제목", example = "Sample Title")
    private String boardTitle;

    @Schema(description = "게시글 작성자", example = "JohnDoe")
    private String boardWriter;

    @Schema(description = "게시글 내용", example = "This is the content of the board post.")
    private String boardContents;

    @Schema(description = "게시글 조회수", example = "15")
    private int boardHits;

    @Schema(description = "게시글 작성 시간", example = "2024-10-30T10:15:30")
    private LocalDateTime boardCreatedTime;

    @Schema(description = "게시글 수정 시간", example = "2024-10-31T11:20:00")
    private LocalDateTime boardUpdatedTime;

    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardId(boardEntity.getBoardId());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());

        return boardDTO;
    }

}
