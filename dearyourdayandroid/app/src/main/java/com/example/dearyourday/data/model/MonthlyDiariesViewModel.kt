package com.example.dearyourday.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.diary.DiaryMonthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

// 일기 모아보기 화면을 위한 ViewModel
class MonthlyDiariesViewModel : ViewModel() {
    // (Key: 날짜, Value: 일기 정보) 형태의 Map으로 저장
    private val _monthlyDiaries = MutableStateFlow<Map<LocalDate, DiaryMonthResponse>>(emptyMap())
    // 읽기 전용
    val monthlyDiaries: StateFlow<Map<LocalDate, DiaryMonthResponse>> = _monthlyDiaries.asStateFlow()

    // 현재 보고 있는 달 (초기값: 오늘 날짜)
    var currentYearMonth by mutableStateOf(YearMonth.now())
        private set

    // 달 변경 시 호출되는 메소드
    fun updateMonth(yearMonth: YearMonth) {
        currentYearMonth = yearMonth
        fetchDiaries(yearMonth)
    }

    // 해당 월의 일기 목록 서버 요청 메소드
    fun fetchDiaries(yearMonth: YearMonth) {
        viewModelScope.launch {
            try {
                // 1. 서버 API 호출
                val response = RetrofitInstance.diaryApi.getDiariesByMonth(
                    userId = UserSession.userId,
                    year = yearMonth.year,
                    month = yearMonth.monthValue
                )

                // API 호출에 성공했을 경우
                if (response.isSuccessful) {
                    // 2. 응답 데이터 추출 (null일 경우 빈 리스트)
                    val realList = response.body() ?: emptyList()

                    // 3. List -> Map 변환
                    val diaryMap = realList.associateBy {
                        // 서버 날짜 포맷("yyyy-MM-dd")을 LocalDate로 변환하여 Key로 사용
                        LocalDate.parse(it.writtenDate)
                    }
                    _monthlyDiaries.value = diaryMap
                }
                // API 호출에 오류가 발생할 경우
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}