package com.wandookong.voice_me_sing.entity;

import com.wandookong.voice_me_sing.dto.BoardSaveDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BoardEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String boardTitle;
    private String boardWriter;

    @Column(length = 1000)
    private String boardContents;

    private int boardHits;

    public static BoardEntity toBoardEntity(BoardSaveDTO boardDTO, String nickname) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardWriter(nickname);
        boardEntity.setBoardHits(0);
        return boardEntity;
    }

}
