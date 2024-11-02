package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.BoardDTO;
import com.wandookong.voice_me_sing.dto.BoardEditDTO;
import com.wandookong.voice_me_sing.dto.BoardSaveDTO;
import com.wandookong.voice_me_sing.entity.BoardEntity;
import com.wandookong.voice_me_sing.entity.UserEntity;
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

    public String save(BoardSaveDTO boardSaveDTO, String accessToken) {
        // accessToken 으로부터 사용자 정보(nickname) 추출
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        if (optionalUserEntity.isEmpty()) return "NO_USER";

        String nickname = optionalUserEntity.get().getNickname();

        System.out.println("nickname = " + nickname);

        boardRepository.save(BoardEntity.toBoardEntity(boardSaveDTO, nickname));

        return "SAVED";
    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();

        for (BoardEntity boardEntity : boardEntityList) {
            System.out.println("boardEntity.getBoardWriter() = " + boardEntity.getBoardWriter());
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }

        return boardDTOList;
    }

//    @Transactional
    public List<BoardDTO> findByUser(String accessToken) {
        String email = jwtUtil.getEmail(accessToken);

//        String nickname = userRepository.findNicknameByEmail(email);

        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        String nickname = optionalUserEntity.get().getNickname();

        List<BoardEntity> boardEntityList = boardRepository.findByBoardWriter(nickname);
        List<BoardDTO> boardDTOList = new ArrayList<>();

        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }

        return boardDTOList;
    }

    public String editPost(String accessToken, BoardEditDTO boardEditDTO) {
        // 1. 수정하려는 게시글 작성자가 맞는지 확인
        String email = jwtUtil.getEmail(accessToken);

        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        String nickname = optionalUserEntity.get().getNickname();

        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardEditDTO.getBoardId()));
        BoardEntity boardEntity = optionalBoardEntity.get();
        String boardWriter = boardEntity.getBoardWriter();

        if (!nickname.equals(boardWriter)) return "BAD_ACCESS";

        // 2. 수정
        boardEntity.setBoardTitle(boardEditDTO.getBoardTitle());
        boardEntity.setBoardContents(boardEditDTO.getBoardContents());

        boardRepository.save(boardEntity);

        return "EDITED";
    }

    public BoardDTO findById(String boardId) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardId));

        if (optionalBoardEntity.isEmpty()) return null;

        BoardEntity boardEntity = optionalBoardEntity.get();

        return BoardDTO.toBoardDTO(boardEntity);
    }

    public boolean checkWriter(String boardId, String accessToken) {
        String email = jwtUtil.getEmail(accessToken);
        String nickname = userRepository.findNicknameByEmail(email);

        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(Long.valueOf(boardId));
        String boardWriter = optionalBoardEntity.get().getBoardWriter();

        return nickname.equals(boardWriter);
    }
}
