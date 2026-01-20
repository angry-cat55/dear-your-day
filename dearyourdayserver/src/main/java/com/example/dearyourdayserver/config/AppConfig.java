package com.example.dearyourdayserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestClient;

@Configuration // 설정 파일임을 알리는 어노테이션
@EnableAsync
public class AppConfig {

    // application.yml에 적은 값
    @Value("${app.ai.gemini.api-key}")
    private String geminiApiKey;

    @Value("${app.ai.gemini.url}")
    private String geminiUrl;

    // RestClient라는 도구를 스프링 빈(Bean)으로 등록하는 과정
    @Bean
    public RestClient geminiRestClient() {
        return RestClient.builder()
                .baseUrl(geminiUrl) // https://generativelanguage.googleapis.com 까지만 설정
                .defaultHeader("x-goog-api-key", geminiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}