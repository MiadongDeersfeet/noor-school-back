package com.noorschool.word.audio.service;

import com.noorschool.common.exception.BusinessException;
import com.noorschool.common.model.vo.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AudioStorageService {

    private static final String FILE_SUFFIX = "_ko_normal.mp3";

    @Value("${file.audio-dir}")
    private String audioDir;

    @Value("${file.audio-url-prefix}")
    private String audioUrlPrefix;

    public SavedAudio saveWordAudio(Long wordId, byte[] audioBytes) {
        if (wordId == null) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "wordId가 없습니다.");
        }
        if (audioBytes == null || audioBytes.length == 0) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "저장할 오디오 데이터가 비어 있습니다.");
        }

        String fileName = wordId + FILE_SUFFIX;
        Path dirPath = Paths.get(audioDir);
        Path filePath = dirPath.resolve(fileName);

        try {
            Files.createDirectories(dirPath);
            Files.write(filePath, audioBytes);
        } catch (IOException ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "오디오 파일 저장에 실패했습니다.");
        }

        String normalizedPrefix = audioUrlPrefix.endsWith("/")
                ? audioUrlPrefix.substring(0, audioUrlPrefix.length() - 1)
                : audioUrlPrefix;
        String audioUrl = normalizedPrefix + "/" + fileName;
        String storageKey = filePath.toString().replace("\\", "/");

        return new SavedAudio(audioUrl, storageKey);
    }

    public record SavedAudio(String audioUrl, String storageKey) {
    }
}
