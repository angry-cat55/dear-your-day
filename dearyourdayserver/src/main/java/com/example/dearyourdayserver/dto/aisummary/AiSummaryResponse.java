package com.example.dearyourdayserver.dto.aisummary;

import com.example.dearyourdayserver.entity.AiSummary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AiSummaryResponse {
    private Long summaryId;
    private String content;      // AI가 해준 종합 위로/공감 멘트
    private LocalDateTime generatedAt; // 언제 생성된 멘트인지

    public static AiSummaryResponse from(AiSummary summary) {
        return AiSummaryResponse.builder()
                .summaryId(summary.getSummaryId())
                .content(summary.getSummaryContent())
                .generatedAt(summary.getUpdatedAt())
                .build();
    }
}