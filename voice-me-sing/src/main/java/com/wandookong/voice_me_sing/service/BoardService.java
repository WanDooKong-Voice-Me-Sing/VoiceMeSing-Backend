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

    public void save(BoardSaveDTO boardSaveDTO, String accessToken) {
        // accessToken 으로부터 사용자 정보(nickname) 추출
        String email = jwtUtil.getEmail(accessToken);
        String nickname = userRepository.getNicknameByEmail(email);

        // nickname 을 바탕으로 게시글 저장
        boardRepository.save(BoardEntity.toBoardEntity(boardSaveDTO, nickname));
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

    // 이거를 accessToken -> nickname 으로 바꿔야 겠다
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

    public boolean editPost(String accessToken, BoardEditDTO boardEditDTO) {
        // 1. 수정하려는 게시글 작성자가 맞는지 확인
        // accessToken 으로부터 사용자 정보(nickname) 추출
        String email = jwtUtil.getEmail(accessToken);
        String nickname = userRepository.getNicknameByEmail(email);

        // boardId 로 해당 게시글 boardWriter(nickname) 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardEditDTO.getBoardId()));
        BoardEntity boardEntity = optionalBoardEntity.get();
        String boardWriter = boardEntity.getBoardWriter();

        if (!nickname.equals(boardWriter)) return false;

        // 2. 수정
        boardEntity.setBoardTitle(boardEditDTO.getBoardTitle());
        boardEntity.setBoardContents(boardEditDTO.getBoardContents());

        boardRepository.save(boardEntity);

        return true;
    }

    public BoardDTO findById(String boardId) {
        // boardId 로 해당 게시글 조회
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardId));

        if (optionalBoardEntity.isEmpty()) return null;
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

        // 삭제
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
