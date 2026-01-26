package com.example.dearyourday.ui.screens.diary

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.diary.DiaryResponse
import com.example.dearyourday.ui.components.DiaryScaffold
import java.time.LocalDate

@Composable
fun MainDiaryScreen(
    navController: NavController,
    targetDate: String = LocalDate.now().toString() // 전달받은 날짜 (디폴트값: 오늘)
) {
    // 로딩 중인지 기억하는 변수
    var isLoading by rememberSaveable { mutableStateOf(true) }
    // 일기 정보를 기억하는 변수
    var diaryData by rememberSaveable { mutableStateOf<DiaryResponse?>(null) }
    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current

    LaunchedEffect(targetDate) {
        // 변수 초기화
        isLoading = true
        diaryData = null

        try {
            // 1. API로 targetDate의 일기 조회
            val response = RetrofitInstance.diaryApi.getDiaryByDate(
                userId = UserSession.userId,
                date = targetDate
            )

            // 2. 결과에 따라 처리
            if (response.body() != null) { // 일기 조회 성공할 경우
                diaryData = response.body()
                isLoading = false
            }
            else { // 조회할 일기가 없을 경우
                navController.navigate("write_diary/$targetDate") {
                    popUpTo("main_diary/$targetDate") { inclusive = true }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "에러 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            isLoading = false;
        }
    }
    
    // 화면 그리기
    DiaryScaffold(
        navController = navController,
        title = "2026.01.26"
    ) { innerPadding ->
        // 로딩 상태일 경우 로딩 화면 출력
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // 로딩 상태가 아닐 경우 일기 화면 출력
        else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Text("메인 화면 (오늘의 일기)", fontSize = 24.sp)

                Text(diaryData!!.content, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(100.dp))

                // 1. 일기 쓰기 테스트 버튼
                Button(onClick = { navController.navigate("write_diary") }) {
                    Text("일기 쓰러 가기 (작성 안 된 날)")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. 모아보기 이동 버튼 (햄버거 메뉴 대용)
                Button(onClick = { navController.navigate("monthly_diaries") }) {
                    Text("일기 모아보기 (캘린더)")
                }

                // 3. 분석 이동 버튼 (햄버거 메뉴 대용)
                Button(onClick = { navController.navigate("diary_summary") }) {
                    Text("종합 공감 보기")
                }
            }
        }
    }
}