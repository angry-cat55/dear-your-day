package com.example.dearyourday.ui.screens.diary

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.DiarySnapshot
import com.example.dearyourday.data.model.Mood
import com.example.dearyourday.data.model.diary.DiaryResponse
import com.example.dearyourday.data.model.diary.DiaryWriteRequest
import com.example.dearyourday.ui.components.ConfirmDialog
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
    // 다이얼로그 표시 여부를 제어할 변수
    var showSaveDialog by remember { mutableStateOf(false) }

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

    // 작성할 일기 날짜 포맷팅
    val formattedDate = LocalDate.parse(targetDate)
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

    DiaryScaffold(
        navController = navController,
        title = "오늘의 하루," + formattedDate
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 상단 타이틀 문구 분기
            var titleText = "";
            if (mode == "edit") {
                titleText = "너의 하루를 다시 얘기해줘."
            } else {
                titleText = "너의 하루를 얘기해줘."
            }
            Text(titleText, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxSize()
                )

                // 내용 없을 때만 중앙 안내 문구
                if (content.isEmpty()) {
                    Text(
                        text = "일기를 쓰면 내 친구 AI가 읽고\n코멘트를 남겨줘요.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "오늘 하루는 어땠어요?",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center, // 가운데 정렬
                verticalArrangement = Arrangement.spacedBy(8.dp)    // 위아래 간격
            ) {
                // Mood Enum을 하나씩 돌면서 칩 생성
                Mood.values().forEach { mood ->
                    FilterChip(
                        selected = (moodCode == mood.name), // 현재 선택된 것인지 확인
                        onClick = { moodCode = mood.name },
                        label = {
                            Text(
                                text = mood.description, // 이모지 설명
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        },
                        leadingIcon = {
                            Text(
                                text = mood.emoji, // 이모지 아이콘
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors( // 선택된 버튼 색 지정
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .width(180.dp)
                            .height(35.dp)
                            .padding(horizontal = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 수정 모드로 들어왔을 경우에만 취소 버튼 활성화
                if (mode == "edit") {
                    // 취소 버튼
                    OutlinedButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("취소")
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                // 저장 버튼
                Button(
                    onClick = {
                        // 일기 저장 가능 확인
                        if (content.isNullOrBlank()) { // null, 길이 0, 공백, 개행문자 모두 true로 반환
                            Toast.makeText(context, "내용을 작성해주세요.", Toast.LENGTH_SHORT).show()
                            return@Button
                        } else if (moodCode.isNullOrBlank()) {
                            Toast.makeText(context, "기분을 선택해주세요.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        showSaveDialog = true
                    },
                    modifier = Modifier
                        .width(270.dp)
                        .height(50.dp)
                ) {
                    Text("저장하기")
                }

                // 저장 다이얼로그 (공통 컴포넌트 호출)
                if (showSaveDialog) {
                    ConfirmDialog(
                        title = "저장 확인", // 제목 변경
                        text = "작성한 내용을 저장하시겠습니까?", // 내용 변경
                        onConfirm = {
                            // 다이얼로그 닫기
                            showSaveDialog = false

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
                                        // 변경 여부 판단
                                        val contentChanged = content != originalDiary.content
                                        val moodChanged = moodCode != originalDiary.moodCode

                                        // 내용이 바뀐 경우 → updateDiary API (AI API 호출)
                                        if (contentChanged) {
                                            val response = RetrofitInstance.diaryApi.updateDiary(
                                                diaryData!!.diaryId, request
                                            )
                                            if (!response.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "일기 수정에 실패했습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@launch
                                            }
                                        }
                                        // 내용은 그대로 + 기분만 바뀐 경우 → 기분 코드만 업데이트하는 API (AI API 미호출)
                                        else if (moodChanged) {
                                            val response = RetrofitInstance.diaryApi.updateMood(
                                                diaryData!!.diaryId,
                                                moodCode
                                            )
                                            if (!response.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "기분 수정에 실패했습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@launch
                                            }
                                        }
                                    }
                                    // 쓰기 상황일 때
                                    else {
                                        val response = RetrofitInstance.diaryApi.writeDiary(request)
                                        if (!response.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "일기 저장에 실패했습니다.",
                                                Toast.LENGTH_SHORT
                                            )
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
                        },
                        onDismiss = { showSaveDialog = false }
                    )
                }
            }
        }
    }
}