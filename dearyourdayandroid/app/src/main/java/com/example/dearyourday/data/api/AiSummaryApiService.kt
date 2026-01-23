package com.example.dearyourday.data.api

import com.example.dearyourday.data.model.aisummary.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AiSummaryApiService {
    // 종합 공감 코멘트 조회
    @GET("/api/aisummary")
    suspend fun getAiSummary(
        @Query("userId") userId: Long
    ): Response<AiSummaryResponse>
}
