package com.example.dearyourday.ui.screens.diary

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.DiarySnapshot
import com.example.dearyourday.data.model.diary.DiaryResponse
import com.example.dearyourday.data.model.diary.DiaryWriteRequest
import com.example.dearyourday.ui.components.DiaryScaffold
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WriteDiaryScreen(
    navController: NavController,
    targetDate: String = LocalDate.now().toString(),
    mode: String = "write"
) {
    // suspend 함수 사용을 위한 객체
    val coroutineScope = rememberCoroutineScope()
    // 작성할 일기 날짜 포맷팅
    val formattedDate = LocalDate.parse(targetDate)
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current

    // 일기 데이터
    var diaryData by rememberSaveable { mutableStateOf<DiaryResponse?>(null) }
    // 일기 내용
    var content by rememberSaveable { mutableStateOf("") }
    // 기분 이모지 코드
    var moodCode by rememberSaveable { mutableStateOf("") }

    // 변경 여부 확인용 클래스 (일기 내용, 기분 이모지 코드)
    var originalDiary by rememberSaveable { mutableStateOf(DiarySnapshot()) }

    // 일기 수정을 위한 화면일 경우
    LaunchedEffect(mode, targetDate) {
        if (mode == "edit") {
            try {
                val response = RetrofitInstance.diaryApi.getDiaryByDate(
                    userId = UserSession.userId,
                    date = targetDate
                )
                // 성공적으로 받아왔을 때만 데이터 채우기
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    diaryData = data
                    content = data.content
                    moodCode = data.moodCode
                    originalDiary = DiarySnapshot(content = data.content, moodCode = data.moodCode)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "데이터 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    DiaryScaffold(
        navController = navController,
        title = formattedDate
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("일기 작성 화면", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = content, onValueChange = { content = it },
                label = { Text("오늘 하루는 어땠나요?") },
                modifier = Modifier.height(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                coroutineScope.launch {
                    // 현재 작업된 새 일기 객체 생성
                    val request = DiaryWriteRequest(
                        userId = UserSession.userId,
                        writtenDate = targetDate,
                        content = content,
                        moodCode = moodCode
                    )
                    try {
                        // 수정 상황일 때
                        if (mode == "edit") {
                            // 일기 데이터 변경 여부 확인 후, 변경이 있으면 DB에 UPDATE
                            val isChanged =
                                content != originalDiary.content || moodCode != originalDiary.moodCode
                            if (isChanged) {
                                val response = RetrofitInstance.diaryApi.updateDiary(
                                    diaryData!!.diaryId, request
                                )
                                if (!response.isSuccessful) {
                                    Toast.makeText(context, "일기 수정에 실패했습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                    return@launch
                                }
                            }
                        }
                        // 쓰기 상황일 때
                        else {
                            val response = RetrofitInstance.diaryApi.writeDiary(request)
                            if (!response.isSuccessful) {
                                Toast.makeText(context, "일기 저장에 실패했습니다.", Toast.LENGTH_SHORT)
                                    .show()
                                return@launch
                            }
                        }

                        // 일기 저장/수정 성공할 경우 -> 메인으로 이동
                        navController.navigate("main_diary/$targetDate") {
                            popUpTo("write_diary/$targetDate") { inclusive = true }
                        }
                    } catch (e: Exception) { // 예외 상황 발생할 경우 (서버 끊김 등)
                        Toast.makeText(context, "에러 발생: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }) {
                Text("저장하기")
            }
        }
    }
}