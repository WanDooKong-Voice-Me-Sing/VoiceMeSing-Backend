package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.aiserver.AiService;
import com.wandookong.voice_me_sing.dto.TrainVoiceDTO;
import com.wandookong.voice_me_sing.dto.VoiceModelDTO;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.entity.VoiceModelEntity;
import com.wandookong.voice_me_sing.entity.VoiceTempEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.UserRepository;
import com.wandookong.voice_me_sing.repository.VoiceModelRepository;
import com.wandookong.voice_me_sing.repository.VoiceTempRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoiceService {

    private final VoiceTempRepository voiceTempRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AiService aiService;
    private final VoiceModelRepository voiceModelRepository;
    private final UserService userService;

    @Value("${spring.savePath}")
    private String path;

    public boolean saveVoiceBlobFile(TrainVoiceDTO trainVoiceDTO, String accessToken) throws IOException {
        // VoiceTempEntity: AI 서버가 음성 모델을 생성하는 데 필요한 정보를 담은 엔티티 (voiceId, voiceFile, voiceModelName, userId)
        VoiceTempEntity voiceTempEntity = new VoiceTempEntity();

        // 값 세팅 (voiceFile, voiceModelName)
        voiceTempEntity.setVoiceFile(trainVoiceDTO.getVoiceFile().getBytes());
        voiceTempEntity.setVoiceModelName(trainVoiceDTO.getVoiceModelName());

        // accessToken 으로부터 userId 얻기
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty()) {
            return false;
        }
        UserEntity userEntity = optionalUserEntity.get();
        Long userId = userEntity.getUserId();

        // 값 세팅 (userId)
        voiceTempEntity.setUserId(String.valueOf(userId));

        // 저장 후 자동 생성되는 id 값 전달
        VoiceTempEntity saved = voiceTempRepository.save(voiceTempEntity);
        String voiceId = String.valueOf(saved.getVoiceId());

//        return aiService.toPythonVoiceModel(voiceId);
        return true;
    }

    // *** 사용자가 없을 경우 null 리턴: 사용자가 있는데 모델이 없을 경우는 어떻게 처리할 건지 문제 -> 우선 사용자 무조건 있다고 가정
    public List<VoiceModelDTO> getVoiceModels(String userToken) {
        // accessToken 으로 사용자 조회
        String email = jwtUtil.getEmail(userToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        assert optionalUserEntity.isPresent();
        UserEntity userEntity = optionalUserEntity.get();

        // 음성 모델 리스트 생성 (voiceModelId, voiceModelName 만 보여줌)
        List<VoiceModelDTO> voiceModelDTOs = userEntity.getVoiceModels().stream()
                .map(vm -> new VoiceModelDTO(vm.getVoiceModelId(), vm.getVoiceModelName()))
                .toList();

        return voiceModelDTOs;
    }

    public boolean deleteVoiceModel(String accessToken, Long voiceModelId) {
        // 해당 아이디의 모델이 존재하는지 확인
        Optional<VoiceModelEntity> optionalVoiceModelEntity = voiceModelRepository.findById(voiceModelId);
        if (optionalVoiceModelEntity.isEmpty()) return false;
        VoiceModelEntity voiceModelEntity = optionalVoiceModelEntity.get();

        // accessToken 으로부터 사용자의 아이디 추출
        Long userId = userService.getUserIdByToken(accessToken);

        // 아이디 비교를 통해 사용자의 모델인지 확인 후 삭제
        if (userId.equals(voiceModelEntity.getUserEntity().getUserId())) {
            voiceModelRepository.deleteById(voiceModelId);
            return true;
        } else return false;
    }

    // 수정 전 메소드 (EFS 사용 가정):
//    public boolean saveVoiceFile(TrainVoiceDTO trainVoiceDTO, String accessToken) throws IOException {
//
//        // 1. 파일 이름 설정 후 지정 폴더에 저장 (temp)
//        MultipartFile multipartFile = trainVoiceDTO.getVoiceFile(); // 파일 추출
//        String originalVoiceFileName = multipartFile.getOriginalFilename(); // 이름 추출
//        String storedVoiceFileName = System.currentTimeMillis() + "_" + originalVoiceFileName; // 저장 이름 설정 (같은 이름으로 또 올릴 수도 있기 때문에 랜덤 이름 설정)
//        String savePath = path + storedVoiceFileName; // 저장 위치 지정
//        multipartFile.transferTo(new File(savePath)); // 해당 위치에 저장
//
////        // 2. 파일 정보 디비 임시 저장 (기존 이름, 저장 이름, 저장 위치)
////        VoiceTempEntity voiceTempEntity = new VoiceTempEntity(originalVoiceFileName, storedVoiceFileName, savePath);
////        VoiceTempEntity savedEntity = voiceTempRepository.save(voiceTempEntity);
//
//        // 2. AI 서버가 필요한 정보 추출 (사용자 아이디, 음성 위치, 생성할 음성 모델 이름)
//        // access 토큰으로부터 사용자 id 추출
//        String email = jwtUtil.getEmail(accessToken);
//        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
//        assert optionalUserEntity.isPresent();
//        UserEntity userEntity = optionalUserEntity.get();
//        Long userId = userEntity.getUserId();
//
//        // 사용자가 설정한 모델 이름
//        String voiceModelName = trainVoiceDTO.getVoiceModelName();
//
//        // 4. 요청
//        return true;
////        return aiService.toPythonVoiceModel(userId, savePath, voiceModelName); // 어떤 사용자가 어떤 음성으로 무슨 모델을
//    }
}
