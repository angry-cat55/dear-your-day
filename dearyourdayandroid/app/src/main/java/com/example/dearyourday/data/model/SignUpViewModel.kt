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

    // 전화번호 갱신
    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber) }
    }

    // 닉네임 갱신
    fun updateNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    // 회원가입 메소드
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