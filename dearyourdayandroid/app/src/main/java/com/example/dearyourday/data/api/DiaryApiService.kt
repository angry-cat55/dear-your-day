package com.example.dearyourday.data.api

import com.example.dearyourday.data.model.diary.*
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate

interface DiaryApiService {
    // 일기 작성
    @POST("/api/diaries")
    suspend fun writeDiary(
        @Body request: DiaryWriteRequest
    ): Response<DiaryResponse>

    // 일기 수정
    @PUT("/api/diaries/{diaryId}")
    suspend fun updateDiary(
        @Path("diaryId") diaryId: Long,
        @Body request: DiaryWriteRequest
    ): Response<DiaryResponse>

    // 일기 삭제
    @DELETE("/api/diaries/{diaryId}")
    suspend fun deleteDiary(
        @Path("diaryId") diaryId: Long,
        @Query("userId") userId: Long
    ): Response<Unit>

    // 특정 날짜 일기 조회
    @GET("/api/diaries")
    suspend fun getDiaryByDate(
        @Query("userId") userId: Long,
        @Query("date") date: String
    ): Response<DiaryResponse>

    // 월별 일기 목록 조회
    @GET("/api/diaries/monthly")
    suspend fun getDiariesByMonth(
        @Query("userId") userId: Long,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<DiaryMonthResponse>>

    // 기분 이모지 수정
    @PATCH("/api/diaries/{diaryId}/mood")
    suspend fun updateMood(
        @Path("diaryId") diaryId: Long,
        @Query("moodCode") moodCode: String
    ): Response<Unit>
}
