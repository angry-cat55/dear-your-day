package com.example.dearyourdayserver.controller;

import com.example.dearyourdayserver.dto.aisummary.AiSummaryResponse;
import com.example.dearyourdayserver.service.AiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aisummary")
@RequiredArgsConstructor
public class AiSummaryController {
    private final AiSummaryService aiSummaryService;

    // 종합 공감 코멘트 조회 API
    @GetMapping
    public ResponseEntity<AiSummaryResponse> getAiSummary(@RequestParam Long userId) {
        AiSummaryResponse response = aiSummaryService.getAiSummary(userId); // 서비스 호출해서 유저에 맞는 AI 코멘트 생성

        // 성공하면 "200 OK" 상태코드와 함께 AI 종합 공감 코멘트 DTO 전달
        return ResponseEntity.ok(response);
    }
}
