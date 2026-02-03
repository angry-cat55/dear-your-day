package com.example.dearyourday.data.model

import androidx.lifecycle.ViewModel
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.user.SignupRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Response

class SignUpViewModel: ViewModel() {
    // 내부 저장용
    private val _uiState = MutableStateFlow(SignupRequest())
    // 읽기 전용
    val uiState = _uiState.asStateFlow()

    // 아이디, 비밀번호 갱신
    fun updateIdPassword(loginId: String, password: String) {
        _uiState.update { it.copy(loginId = loginId, password = password) }
    }

    // 이메일 갱신
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    // 닉네임 갱신
    fun updateNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    // 아이디 중복 체크 메소드
    suspend fun requestCheckId(loginId: String): Response<Boolean> {
        // 아이디 중복 확인 후 결과값 저장
        val response = RetrofitInstance.userApi.checkId(loginId)

        // 결과 반환
        return response
    }
    
    // 계정 정보 저장 메소드
    suspend fun requestSignUp(): Response<Unit> {
        // 현재 뷰모델에 저장된 SignupRequest 객체 꺼내기
        val data = _uiState.value

        // 회원가입 API 호출 후 결과 저장
        val response = RetrofitInstance.userApi.signup(data)

        // 결과 반환
        return response
    }

    // 초기화 메소드
    fun clearData() {
        _uiState.value = SignupRequest()
    }
}