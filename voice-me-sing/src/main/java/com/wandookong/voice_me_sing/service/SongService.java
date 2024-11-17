package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.aiserver.AiService;
import com.wandookong.voice_me_sing.dto.CoverSongDTO;
import com.wandookong.voice_me_sing.dto.CoverSongIdDTO;
import com.wandookong.voice_me_sing.dto.CreateSongDTO;
import com.wandookong.voice_me_sing.entity.CoverSongEntity;
import com.wandookong.voice_me_sing.entity.SongTempEntity;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.CoverSongRepository;
import com.wandookong.voice_me_sing.repository.SongTempRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final UserService userService;

    @Value("${spring.savePath}")
    private String path;

    public boolean saveSongBlobFile(CreateSongDTO createSongDTO, String accessToken) throws IOException {
        // SongTempEntity: AI 서버가 커버곡을 생성하는 데 필요한 정보를 담은 엔티티 (songId, coverSongFile, resultSongName, voiceModelId, userId)
        SongTempEntity songTempEntity = new SongTempEntity();

        // 값 세팅
        songTempEntity.setCoverSongFile(createSongDTO.getSongFile().getBytes());
        songTempEntity.setResultSongName(createSongDTO.getResultSongName());
        songTempEntity.setVoiceModelId(createSongDTO.getVoiceModelId());

        // accessToken 으로부터 userId 얻기
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty()) {
            return false;
        }
        UserEntity userEntity = optionalUserEntity.get();
        Long userId = userEntity.getUserId();

        // 값 세팅 (userId)
        songTempEntity.setUserId(String.valueOf(userId));

        // 저장 후 자동 생성되는 id 값 전달
        SongTempEntity saved = songTempRepository.save(songTempEntity);
        String coverSongId = String.valueOf(saved.getSongId());

        aiService.toPythonCoverSong(coverSongId);
        return true;
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
                .map(cs -> new CoverSongDTO(cs.getCoverSongId(), cs.getResultSongName(), cs.getCoverSongFile(), cs.isPublic()))
                .toList();

        return coverSongDTOs;
    }

    public boolean deleteCoverSong(String accessToken, Long coverSongId) {
        // 해당 아이디의 커버곡이 존재하는지 확인
        Optional<CoverSongEntity> optionalCoverSongEntity = coverSongRepository.findById(coverSongId);
        if (optionalCoverSongEntity.isEmpty()) return false;
        CoverSongEntity coverSongEntity = optionalCoverSongEntity.get();

        // accessToken 으로부터 사용자의 아이디 추출
        Long userId = userService.getUserIdByToken(accessToken);

        // 아이디 비교를 통해 사용자의 커버곡인지 확인 후 삭제
        if (userId.equals(coverSongEntity.getUserEntity().getUserId())) {
            coverSongRepository.deleteById(coverSongId);
            return true;
        } else return false;

    }

    public Object togglePublicStatus(String accessToken, CoverSongIdDTO coverSongIdDTO) {
        // 해당 아이디의 커버곡이 존재하는지 확인
        Optional<CoverSongEntity> optionalCoverSongEntity = coverSongRepository.findById(Long.valueOf(coverSongIdDTO.getCoverSongId()));
        if (optionalCoverSongEntity.isEmpty()) return "";
        CoverSongEntity coverSongEntity = optionalCoverSongEntity.get();

        // accessToken 으로부터 사용자의 아이디 추출
        Long userId = userService.getUserIdByToken(accessToken);

        // 아이디 비교를 통해 사용자의 커버곡인지 확인 후 공개 여부 토글 반영
        if (userId.equals(coverSongEntity.getUserEntity().getUserId())) {
            coverSongEntity.setPublic(!coverSongEntity.isPublic());
            coverSongRepository.save(coverSongEntity);
            return coverSongEntity.isPublic();
        } else return "";
    }

    public List<CoverSongDTO> findAllCoverSongs() {
        // 커버곡 리스트 생성
        List<CoverSongDTO> coverSongDTOs = coverSongRepository.findAllByIsPublic(true).stream()
                .map(cs -> new CoverSongDTO(cs.getCoverSongId(), cs.getResultSongName(), cs.getCoverSongFile(), cs.isPublic()))
                .toList();

        return coverSongDTOs;
    }

    // 수정 전 메소드 (EFS 사용 가정):
//    public boolean createCoverSong(CreateSongDTO createSongDTO, String accessToken) throws IOException {
//        // 1. 음원 파일 이름 설정 후 지정 폴더에 저장 (temp)
//        MultipartFile multipartFile = createSongDTO.getSongFile(); // 파일 추출
//        String originalSongFileName = multipartFile.getOriginalFilename(); // 이름 추출
//        String storedSongFileName = System.currentTimeMillis() + "_" + originalSongFileName; // 저장 이름 설정
//        String savePath = path + storedSongFileName; // 저장 위치 지정
//        multipartFile.transferTo(new File(savePath)); // 해당 위치에 저장
//
////        // 2. 파일 디비 임시 저장
////        SongTempEntity songTempEntity = new SongTempEntity(originalSongFileName, storedSongFileName, savePath);
////        SongTempEntity savedEntity = songTempRepository.save(songTempEntity);
//
////        Long songId = savedEntity.getSongId();
//
//        // 2. AI 서버가 필요한 정보 추출 (userId, savePath, voiceModelID, resultSongName)
//
//        // access 토큰으로부터 사용자 id 뽑아내기
//        String email = jwtUtil.getEmail(accessToken);
//        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
//        assert optionalUserEntity.isPresent();
//        UserEntity userEntity = optionalUserEntity.get();
//        Long userId = userEntity.getUserId();
//
//        Long voiceModelId = createSongDTO.getVoiceModelId();
//
//        String resultSongName = createSongDTO.getResultSongName();
//
//        // 4. 요청
//        return true;
////        return aiService.toPythonCoverSong(userId, savePath, voiceModelId, resultSongName);
//    }
}
