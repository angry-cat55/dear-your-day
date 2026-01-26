package com.example.dearyourday.data.model.diary

import java.time.LocalDate
import java.time.LocalDateTime

// 월별 일기 목록 응답
data class DiaryMonthResponse(
    val diaryId: Long,
    val writtenDate: String,
    val moodCode: String
)

// 특정 일기 내용 응답
data class DiaryResponse(
    val diaryId: Long,
    val content: String,
    val writtenDate: String,
    val moodCode: String,
    val updatedAt: String,
    val aiComment: String?
)

// 일기 작성 요청
data class DiaryWriteRequest(
    val userId: Long,
    val writtenDate: String,
    val content: String,
    val moodCode: String
)