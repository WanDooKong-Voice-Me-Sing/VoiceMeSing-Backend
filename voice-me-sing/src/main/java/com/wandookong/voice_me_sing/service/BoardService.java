package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.BoardSaveDTO;
import com.wandookong.voice_me_sing.entity.BoardEntity;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.BoardRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        boardRepository.save(BoardEntity.toBoardEntity(boardSaveDTO, nickname));

        return "SAVED";
    }
}
