package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.BoardDTO;
import com.wandookong.voice_me_sing.dto.BoardEditDTO;
import com.wandookong.voice_me_sing.dto.BoardSaveDTO;
import com.wandookong.voice_me_sing.entity.BoardEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.BoardRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public BoardDTO save(BoardSaveDTO boardSaveDTO, String accessToken) {
        // accessToken 으로부터 사용자 정보(nickname) 추출
        String email = jwtUtil.getEmail(accessToken);
        String nickname = userRepository.getNicknameByEmail(email);

        // nickname 과 boardSaveDTO(게시글 제목과 내용)을 바탕으로 게시글 저장
        BoardEntity boardEntity = boardRepository.save(BoardEntity.toBoardEntity(boardSaveDTO, nickname));
        return BoardDTO.toBoardDTO(boardEntity);
    }

    public List<BoardDTO> findAll() {
        // 전체 게시글 리스트
        List<BoardEntity> boardEntityList = boardRepository.findAll();

        // 전체 게시글 엔티티 -> DTO 변환
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }

        return boardDTOList;
    }

    public List<BoardDTO> findByNickname(String nickname) {
        // nickname 으로 작성된 게시글 리스트 조회
        List<BoardEntity> boardEntityList = boardRepository.findByBoardWriter(nickname);

        // 전체 게시글 엔티티 -> DTO 변환
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }

        return boardDTOList;
    }

    public BoardDTO editPost(String accessToken, BoardEditDTO boardEditDTO) {
        // 1. 수정하려는 게시글 작성자가 맞는지 확인 (checkWriter()와 로직 같음)
        // accessToken 으로부터 사용자 정보(nickname) 추출
        String email = jwtUtil.getEmail(accessToken);
        String nickname = userRepository.getNicknameByEmail(email);

        // boardId 로 해당 게시글 boardWriter(nickname) 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardEditDTO.getBoardId()));
        BoardEntity boardEntity = optionalBoardEntity.get();
        String boardWriter = boardEntity.getBoardWriter();

        // 게시글 작성자와 수정 요청자가 다르면 null 리턴
        if (!nickname.equals(boardWriter)) return null;

        // 2. 수정 반영
        boardEntity.setBoardTitle(boardEditDTO.getBoardTitle());
        boardEntity.setBoardContents(boardEditDTO.getBoardContents());

        BoardEntity newBoardEntity = boardRepository.save(boardEntity);
        return BoardDTO.toBoardDTO(newBoardEntity);
    }

    public BoardDTO findById(String boardId) {
        // boardId 로 해당 게시글 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardId));

        // 게시글이 존재하지 않을 경우 null 리턴
        if (optionalBoardEntity.isEmpty()) return null;

        // 게시글이 존재할 경우 조회수 업데이트, DTO로 변환 후 리턴
        updateBoardHits(boardId);
        BoardEntity boardEntity = optionalBoardEntity.get();

        return BoardDTO.toBoardDTO(boardEntity);
    }

    public boolean checkWriter(String boardId, String accessToken) {
        // accessToken 으로부터 사용자 정보(nickname) 추출
        String email = jwtUtil.getEmail(accessToken);
        String nickname = userRepository.getNicknameByEmail(email);

        // boardId 로 해당 게시글 boardWriter(nickname) 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardId));
        if (optionalBoardEntity.isEmpty()) return false;
        String boardWriter = optionalBoardEntity.get().getBoardWriter();

        // 일치 여부 리턴
        return nickname.equals(boardWriter);
    }

    public boolean delete(String boardId, String accessToken) {
        // 삭제하려는 게시글 작성자가 맞는지 확인
        boolean isWriter = checkWriter(boardId, accessToken);
        if (!isWriter) return false;

        // 해당 아이디의 게시글 삭제
        boardRepository.deleteById(Long.valueOf(boardId));

        return true;
    }

    public void updateBoardHits(String boardId) {
        // boardId 로 해당 게시글 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardId));

        if (optionalBoardEntity.isEmpty()) return;
        BoardEntity boardEntity = optionalBoardEntity.get();

        // boardHits 업데이트
        boardEntity.setBoardHits(boardEntity.getBoardHits() + 1);

        boardRepository.save(boardEntity);
    }

    public void changeBoardWriter(String oldNickname, String newNickname) {
        List<BoardEntity> boardEntityList = boardRepository.findByBoardWriter(oldNickname);

        for (BoardEntity boardEntity : boardEntityList) {
            boardEntity.setBoardWriter(newNickname);
            boardRepository.save(boardEntity);
        }
    }
}
