package com.example.dearyourday.ui.screens.diary

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.Mood
import com.example.dearyourday.data.model.diary.DiaryResponse
import com.example.dearyourday.ui.components.ConfirmDialog
import com.example.dearyourday.ui.components.DiaryScaffold
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    // suspend 함수 사용을 위한 객체
    val coroutineScope = rememberCoroutineScope()
    // 다이얼로그 표시 여부를 제어할 변수
    var showDeleteDialog by remember { mutableStateOf(false) }

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
            } else { // 조회할 일기가 없을 경우
                navController.navigate("write_diary/$targetDate?mode=write") {
                    popUpTo("main_diary/$targetDate") { inclusive = true }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "에러 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            isLoading = false;
        }
    }

    // 작성할 일기 날짜 포맷팅
    val formattedDate = LocalDate.parse(targetDate)
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

    // 화면 그리기
    DiaryScaffold(
        navController = navController,
        title = "오늘의 하루," + formattedDate
    ) { innerPadding ->
        // 로딩 상태일 경우 로딩 화면 출력
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // 로딩 상태가 아닐 경우 일기 화면 출력
        else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // 상단 타이틀 문구
                Text("나의 하루는,", fontSize = 20.sp)

                Spacer(modifier = Modifier.height(20.dp))

                // 조회한 일기 데이터 적용
                diaryData?.let { diary -> // it 대신 명확하게 diary로 명명
                    // 일기 내용
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(4.dp)) // 내용 + 스크롤 영역 자르기
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Text(
                            text = diary.content,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 일기 관련 정보 및 수정/삭제 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 좌측 영역
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 기분 이모지
                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = Mood.from(diary.moodCode)?.emoji ?: "?",
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // 작성 혹은 최근 수정 시간
                            // LocalDateTime -> String 포맷팅
                            val formattedDateTime = LocalDateTime.parse(diary.updatedAt)
                                .format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm"))
                            Text(
                                text = formattedDateTime,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // 우측 영역
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 일기 수정 버튼
                            TextButton(
                                onClick = {
                                    navController.navigate("write_diary/$targetDate?mode=edit")
                                },
                                modifier = Modifier.height(35.dp)
                            ) {
                                Text("수정")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // 일기 삭제 버튼
                            TextButton(
                                onClick = {
                                    // 삭제 확인 다이얼로그 띄우기
                                    showDeleteDialog = true
                                },
                                modifier = Modifier.height(35.dp)
                            ) {
                                Text("삭제")
                            }

                            // 삭제 다이얼로그 (컴포넌트 호출)
                            if (showDeleteDialog) {
                                ConfirmDialog(
                                    title = "삭제 확인",
                                    text = "정말로 삭제하시겠습니까?",
                                    onConfirm = {
                                        // 다이얼로그 닫기
                                        showDeleteDialog = false

                                        coroutineScope.launch {
                                            try {
                                                // 일기 삭제 API 요청 및 응답
                                                val response = RetrofitInstance.diaryApi
                                                    .deleteDiary(diary.diaryId, UserSession.userId)

                                                // 삭제에 실패할 경우
                                                if (!response.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "삭제를 실패했습니다.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    return@launch
                                                }

                                                // 삭제에 성공할 경우 전 화면(하루 보관함 or 오늘의 하루)으로 이동
                                                // TODO: 일단 오늘의 하루로 이동을 하지만, 추후에 전 화면 구분 로직 구현할 것
                                                if (true/*전 화면이 오늘의 하루일 때*/) {
                                                    // 현재 화면 스택 하나 제거
                                                    navController.popBackStack()
                                                    // 오늘의 하루로 이동
                                                    navController.navigate("main_diary/$targetDate")
                                                }

                                            } catch (e: Exception) { //예외 상황 발생할 경우 (서버 끊김 등)
                                                Toast.makeText(
                                                    context,
                                                    "에러 발생: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    onDismiss = { showDeleteDialog = false }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // AI 코멘트 부분 문구
                    Text("너의 하루에게.", fontSize = 20.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    // AI 코멘트 생성 여부 판단
                    // 비어 있을 경우
                    if (diary.aiComment.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    // 생성되어 있을 경우
                    else {
                        // AI 코멘트 내용
                        Box(
                            modifier = Modifier
                                .weight(1f) // 남는 공간 전부
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp)) // 내용 + 스크롤 영역 자르기
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            Text(
                                text = diary.aiComment,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }
}