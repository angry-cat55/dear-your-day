package com.example.dearyourday.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.aisummary.AiSummaryResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SummaryUiState {
    object Initial: SummaryUiState // 초기 상태
    object Loading: SummaryUiState // 로딩 중
    data class Success(val summary: AiSummaryResponse): SummaryUiState // 성공
    data class Error(val message: String): SummaryUiState // 실패
}

class AiSummaryViewModel: ViewModel() {
    // UI의 상태 저장
    private val _uiState = MutableStateFlow<SummaryUiState>(SummaryUiState.Initial)
    // 읽기 전용
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    fun getAiSummary() {
        viewModelScope.launch { 
            // 뷰모델 사용 화면을 로딩 상태로 변경
            _uiState.value = SummaryUiState.Loading

            // 최소 로딩 시간
            delay(2000)

            try {
                // 종합 공감 코멘트 API 호출
                val response = RetrofitInstance.aiApi.getAiSummary(UserSession.userId)

                // 불러오기 성공할 경우
                if (response.isSuccessful && response.body() != null) {
                    // 본문 데이터 전달
                    _uiState.value = SummaryUiState.Success(response.body()!!)
                }
                // 불러오기 실패할 경우
                else {
                    _uiState.value = SummaryUiState.Error("일기들을 읽지 못했어요.")
                }
            } catch(e: Exception) {
                _uiState.value = SummaryUiState.Error("오류가 발생했습니다: ${e.message}")
            }
        }
    }
}