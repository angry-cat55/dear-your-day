package com.example.dearyourday.data.api

import com.example.dearyourday.data.model.user.*
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    // 회원가입
    @POST("/api/users/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<Long>

    // 아이디 중복 확인
    @GET("/api/users/checkId")
    suspend fun checkId(
        @Query("loginId") loginId: String
    ): Response<Boolean>

    // 로그인
    @POST("/api/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 내 정보 조회
    @GET("/api/users/{userId}")
    suspend fun getInfoById(
        @Path("userId") userId: Long
    ): Response<MyInfoResponse>

    // 닉네임 수정
    @PATCH("/api/users/{userId}/nickname")
    suspend fun updateNickname(
        @Path("userId") userId: Long,
        @Body request: NicknameUpdateRequest
    ): Response<String>
}