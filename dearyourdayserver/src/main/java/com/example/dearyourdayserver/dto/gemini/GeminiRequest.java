package com.example.dearyourdayserver.dto.gemini;

import java.util.List;

// Gemini API 요청 포맷: { "contents": [ { "parts": [ { "text": "..." } ] } ] }
public record GeminiRequest(List<Content> contents) {

    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    public static GeminiRequest of(String text) {
        return new GeminiRequest(
                List.of(new Content(List.of(new Part(text))))
        );
    }
}