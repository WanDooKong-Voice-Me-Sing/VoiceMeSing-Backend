package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.aiserver.AiService;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @Value("${spring.savePath}")
    private String path;

    public boolean saveVoiceFile(TrainVoiceDTO trainVoiceDTO, String accessToken) throws IOException {

        // 1. 파일 이름 설정 후 지정 폴더에 저장 (temp)
        MultipartFile multipartFile = trainVoiceDTO.getVoiceFile(); // 파일 추출
        String originalVoiceFileName = multipartFile.getOriginalFilename(); // 이름 추출
        String storedVoiceFileName = System.currentTimeMillis() + "_" + originalVoiceFileName; // 저장 이름 설정 (같은 이름으로 또 올릴 수도 있기 때문에 랜덤 이름 설정)
        String savePath = path + storedVoiceFileName; // 저장 위치 지정
        multipartFile.transferTo(new File(savePath)); // 해당 위치에 저장

//        // 2. 파일 정보 디비 임시 저장 (기존 이름, 저장 이름, 저장 위치)
//        VoiceTempEntity voiceTempEntity = new VoiceTempEntity(originalVoiceFileName, storedVoiceFileName, savePath);
//        VoiceTempEntity savedEntity = voiceTempRepository.save(voiceTempEntity);

        // 2. AI 서버가 필요한 정보 추출 (사용자 아이디, 음성 위치, 생성할 음성 모델 이름)
        // access 토큰으로부터 사용자 id 추출
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        assert optionalUserEntity.isPresent();
        UserEntity userEntity = optionalUserEntity.get();
        Long userId = userEntity.getUserId();

        // 사용자가 설정한 모델 이름
        String voiceModelName = trainVoiceDTO.getModelName();

        // 4. 요청
        return true;
//        return aiService.toPythonVoiceModel(userId, savePath, voiceModelName); // 어떤 사용자가 어떤 음성으로 무슨 모델을
    }

    public List<VoiceModelDTO> getVoiceModels(String userToken) {
        // access 토큰으로부터 사용자 id 추출
        String email = jwtUtil.getEmail(userToken);

        // 사용자 검색
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty()) {
            return null;
        }
        UserEntity userEntity = optionalUserEntity.get();

        // 사용자 소유 VoiceModels 의 DTO 리스트 생성
        List<VoiceModelDTO> voiceModelDTOs = userEntity.getVoiceModels().stream()
                .map(vm -> new VoiceModelDTO(vm.getVoiceModelId(), vm.getVoiceModelName()))
                .toList();

        return voiceModelDTOs;
    }

    public boolean deleteVoiceModel(Long voiceModelId) {
        Optional<VoiceModelEntity> optional = voiceModelRepository.findById(voiceModelId);

        if (optional.isPresent()) {
            voiceModelRepository.deleteById(voiceModelId);
            return true;
        }
        return false;
    }
}
