package com.noorschool.word.audio.service;

import com.noorschool.common.exception.BusinessException;
import com.noorschool.common.model.vo.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class AzureTtsService {

    private static final String XML_LANG = "ko-KR";
    private static final String USER_AGENT = "noorschool";

    private final HttpClient httpClient;

    @Value("${azure.speech.key}")
    private String speechKey;

    @Value("${azure.speech.region}")
    private String region;

    @Value("${azure.speech.voice-name}")
    private String voiceName;

    @Value("${azure.speech.output-format}")
    private String outputFormat;

    public AzureTtsService() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public byte[] synthesizeKoreanWord(String text) {
        if (text == null || text.isBlank()) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "TTS 변환할 텍스트가 비어 있습니다.");
        }
        if (speechKey == null || speechKey.isBlank()) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "Azure Speech Key 설정이 비어 있습니다.");
        }
        if (region == null || region.isBlank()) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "Azure Speech Region 설정이 비어 있습니다.");
        }

        String normalizedRegion = region.trim().toLowerCase();
        if (!normalizedRegion.matches("^[a-z0-9-]+$")) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "Azure Speech Region 형식이 올바르지 않습니다.");
        }

        String endpoint = "https://" + normalizedRegion + ".tts.speech.microsoft.com/cognitiveservices/v1";
        String ssml = buildSsml(text);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Ocp-Apim-Subscription-Key", speechKey)
                .header("X-Microsoft-OutputFormat", outputFormat)
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/ssml+xml; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(ssml, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            int status = response.statusCode();
            byte[] body = response.body();

            if (status != 200) {
                String errorBody = body != null ? new String(body, StandardCharsets.UTF_8) : "(empty)";
                if (errorBody.length() > 500) errorBody = errorBody.substring(0, 500);
                log.warn("Azure TTS 오류 status={}, body={}", status, errorBody);
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "Azure TTS 호출 실패 (status: " + status + ")");
            }

            if (body == null || body.length == 0) {
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "Azure TTS 응답이 비어 있습니다.");
            }

            return body;

        } catch (BusinessException ex) {
            throw ex;
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Azure TTS 네트워크 오류: {}", ex.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "Azure TTS 호출에 실패했습니다.");
        }
    }

    public String getVoiceName() {
        return voiceName;
    }

    private String buildSsml(String text) {
        return "<speak version='1.0' xml:lang='" + XML_LANG + "'>"
                + "<voice xml:lang='" + XML_LANG + "' name='" + escapeXml(voiceName) + "'>"
                + escapeXml(text)
                + "</voice>"
                + "</speak>";
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
