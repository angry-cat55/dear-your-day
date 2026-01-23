package com.example.dearyourday.data.model.diary

import java.time.LocalDate
import java.time.LocalDateTime

// 월별 일기 목록 응답
data class MonthResponse(
    val diaryId: Long,
    val writtenDate: LocalDate,
    val moodCode: String
)

// 특정 일기 내용 응답
data class Response(
    val diaryId: Long,
    val content: String,
    val writtenDate: LocalDate,
    val moodCode: String,
    val updatedAt: LocalDateTime,
    val aiComment: String?
)

// 일기 작성 요청
data class WriteRequset(
    val userId: Long,
    val writtenDate: LocalDate,
    val content: String,
    val moodCode: String
)