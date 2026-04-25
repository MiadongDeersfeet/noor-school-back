package com.noorschool.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.audio-dir:uploads/audio/words}")
    private String audioDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String normalizedAudioDir = audioDir.endsWith("/") ? audioDir : audioDir + "/";
        registry.addResourceHandler("/audio/words/**")
                .addResourceLocations("file:" + normalizedAudioDir);
    }
}
