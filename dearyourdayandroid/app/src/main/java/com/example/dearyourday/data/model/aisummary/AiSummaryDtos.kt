package com.example.dearyourday.data.model.aisummary

import java.time.LocalDateTime

// 종합 공감 코멘트 조회 응답
data class AiSummaryResponse(
    val summaryId: Long,
    val content: String,
    val updatedAt: String
)
