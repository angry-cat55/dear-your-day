package com.example.dearyourdayserver.dto.gemini;

import java.util.List;

// Gemini API 응답 포맷 중 필요한 것만 매핑
public record GeminiResponse(List<Candidate> candidates) {

    public record Candidate(Content content) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    // 편의 메서드: 응답에서 텍스트만 추출 (null 처리 포함)
    public String getText() {
        if (candidates == null || candidates.isEmpty() ||
                candidates.get(0).content() == null ||
                candidates.get(0).content().parts().isEmpty()) {
            return "AI 응답을 받아오지 못했습니다.";
        }
        return candidates.get(0).content().parts().get(0).text();
    }
}