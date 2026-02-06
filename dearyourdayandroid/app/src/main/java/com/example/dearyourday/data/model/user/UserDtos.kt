package com.example.dearyourday.data.model.user

// 로그인 요청
data class LoginRequest(
    val loginId: String,
    val password: String
)

// 로그인 응답
data class LoginResponse(
    val userId: Long,
    val nickname: String,
)

// 회원가입 요청
data class SignupRequest(
    val loginId: String = "",
    val password: String = "",
    val nickname: String = "",
    val email: String = ""
)

// 내 정보 조회 응답
data class MyInfoResponse(
    val userId: Long,
    val loginId: String,
    val nickname: String,
    val email: String,
    val createdAt: String?
)

// 닉네임 변경 요청
data class NicknameUpdateRequest(
    val nickname: String
)

// 회원가입 이메일 인증번호 요청
data class SendEmailRequest(
    val email: String
)

// 회원가입 이메일 인증번호 검증 응답
data class VerifyEmailResponse(
    val email: String,
    val authCode: String
)