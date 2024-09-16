package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.aiserver.AiService;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.TrainVoiceDTO;
import com.wandookong.voice_me_sing.dto.VoiceModelDTO;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.entity.VoiceTempEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.UserRepository;
import com.wandookong.voice_me_sing.repository.VoiceTempRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoiceService {

    private final VoiceTempRepository voiceTempRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AiService aiService;

    public boolean saveVoiceFile(TrainVoiceDTO trainVoiceDTO, String accessToken) throws IOException {

        // 1. 파일 이름 설정 후 지정 폴더에 저장 (temp)
        MultipartFile multipartFile = trainVoiceDTO.getVoiceFile(); // 파일 추출
        String originalVoiceFileName = multipartFile.getOriginalFilename(); // 이름 추출
        String storedVoiceFileName = System.currentTimeMillis() + "_" + originalVoiceFileName; // 저장 이름 설정 (같은 이름으로 또 올릴 수도 있기 때문에 랜덤 이름 설정)
        String savePath = "C:\\Users\\RKB\\Desktop\\새 폴더\\" + storedVoiceFileName; // *** // 저장 폴더 지정
        multipartFile.transferTo(new File(savePath)); // 저장

        // 2. 파일 디비 임시 저장
        VoiceTempEntity voiceTempEntity = new VoiceTempEntity(originalVoiceFileName, storedVoiceFileName, savePath);
        VoiceTempEntity savedEntity = voiceTempRepository.save(voiceTempEntity);

        // 3. python server 가 필요한 정보 추출 (사용자 아이디, 음성 아이디, 생성할 음성 모델 이름)
        // 음성 모델 생성에 사용할 음성 아이디
        Long voiceId = savedEntity.getVoiceId();

        // 사용자가 설정한 모델 이름
        String voiceModelName = trainVoiceDTO.getModelName();

        // access 토큰으로부터 사용자 id 추출
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        assert optionalUserEntity.isPresent();
        UserEntity userEntity = optionalUserEntity.get();
        Long userId = userEntity.getUserId();

        // 4. 요청
        return true;
//        return aiService.toPythonVoiceModel(userId, voiceId, voiceModelName); // 어떤 사용자가 어떤 음원으로 무슨 모델을
    }

    public ResponseEntity<?> getVoiceModels(String userToken) {
        // access 토큰으로부터 사용자 id 추출
        String email = jwtUtil.getEmail(userToken);

        // 사용자 검색
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<String>("fail", "no user", null));
        }
        UserEntity userEntity = optionalUserEntity.get();

        // 사용자 소유 VoiceModels 의 DTO 리스트 생성
        List<VoiceModelDTO> voiceModelDTOs = userEntity.getVoiceModels().stream()
                .map(vm -> new VoiceModelDTO(vm.getVoiceModelId(), vm.getVoiceModelName()))
                .toList();

        // 응답 데이터 생성
        ResponseDTO<List<VoiceModelDTO>> responseDTO = new ResponseDTO<>("success", "get voice models successfully", voiceModelDTOs);

        return ResponseEntity.ok().body(responseDTO);

    }
}
