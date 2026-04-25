package com.noorschool.word.audio.controller;

import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.common.model.vo.ResultCode;
import com.noorschool.word.audio.model.dto.WordAudioBatchResponseDTO;
import com.noorschool.word.audio.model.vo.WordAudioVO;
import com.noorschool.word.audio.service.WordAudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/words")
public class AdminWordAudioController {

    private final WordAudioService wordAudioService;

    @PostMapping("/{wordId}/audio")
    public ResponseEntity<ApiResponseDTO<WordAudioVO>> generateWordAudio(@PathVariable Long wordId) {
        WordAudioVO audio = wordAudioService.generateWordAudio(wordId);
        return ApiResponseDTO.toResponseEntity(ResultCode.SUCCESS, "단어 음성 생성 성공", audio);
    }

    @PostMapping("/audio/batch")
    public ResponseEntity<ApiResponseDTO<WordAudioBatchResponseDTO>> generateAllMissingWordAudios() {
        WordAudioBatchResponseDTO result = wordAudioService.generateAllMissingWordAudios();
        return ApiResponseDTO.toResponseEntity(ResultCode.SUCCESS, "단어 음성 일괄 생성 완료", result);
    }
}
