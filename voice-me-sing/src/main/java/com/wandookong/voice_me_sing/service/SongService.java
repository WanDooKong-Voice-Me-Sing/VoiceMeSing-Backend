package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.aiserver.AiService;
import com.wandookong.voice_me_sing.dto.CoverSongDTO;
import com.wandookong.voice_me_sing.dto.CreateSongDTO;
import com.wandookong.voice_me_sing.entity.CoverSongEntity;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.CoverSongRepository;
import com.wandookong.voice_me_sing.repository.SongTempRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final AiService aiService;
    private final SongTempRepository songTempRepository;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final CoverSongRepository coverSongRepository;

    @Value("${spring.savePath}")
    private String path;

    public boolean createCoverSong(CreateSongDTO createSongDTO, String accessToken) throws IOException {
        // 1. 음원 파일 이름 설정 후 지정 폴더에 저장 (temp)
        MultipartFile multipartFile = createSongDTO.getSongFile(); // 파일 추출
        String originalSongFileName = multipartFile.getOriginalFilename(); // 이름 추출
        String storedSongFileName = System.currentTimeMillis() + "_" + originalSongFileName; // 저장 이름 설정
        String savePath = path + storedSongFileName; // 저장 위치 지정
        multipartFile.transferTo(new File(savePath)); // 해당 위치에 저장

//        // 2. 파일 디비 임시 저장
//        SongTempEntity songTempEntity = new SongTempEntity(originalSongFileName, storedSongFileName, savePath);
//        SongTempEntity savedEntity = songTempRepository.save(songTempEntity);

//        Long songId = savedEntity.getSongId();

        // 2. AI 서버가 필요한 정보 추출 (userId, savePath, voiceModelID, resultSongName)

        // access 토큰으로부터 사용자 id 뽑아내기
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        assert optionalUserEntity.isPresent();
        UserEntity userEntity = optionalUserEntity.get();
        Long userId = userEntity.getUserId();

        Long voiceModelId = createSongDTO.getVoiceModelId();

        String resultSongName = createSongDTO.getResultSongName();

        // 4. 요청
        return true;
//        return aiService.toPythonCoverSong(userId, savePath, voiceModelId, resultSongName);
    }

    // *** 사용자가 없을 경우 null 리턴: 사용자가 있는데 모델이 없을 경우는 어떻게 처리할 건지 문제 -> 우선 사용자 무조건 있다고 가정
    public List<CoverSongDTO> getCoverSongs(String accessToken) {
        // accessToken 으로 사용자 조회
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        assert optionalUserEntity.isPresent();
        UserEntity userEntity = optionalUserEntity.get();

        // 커버곡 리스트 생성
        List<CoverSongDTO> coverSongDTOs = userEntity.getCoverSongs().stream()
                .map(cs -> new CoverSongDTO(cs.getCoverSongId(), cs.getCoverSongName()))
                .toList();

        return coverSongDTOs;
    }

    public boolean deleteCoverSong(Long coverSongId) {
        // 존재하는지 확인
        Optional<CoverSongEntity> optionalCoverSongEntity = coverSongRepository.findById(coverSongId);

        // 존재할 경우 삭제
        if (optionalCoverSongEntity.isPresent()) {
            coverSongRepository.deleteById(coverSongId);
            return true;
        } else return false;

    }
}
