package com.example.dearyourday.ui.screens.diary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.model.AiSummaryViewModel
import com.example.dearyourday.data.model.SummaryUiState
import com.example.dearyourday.data.model.aisummary.AiSummaryResponse
import com.example.dearyourday.ui.components.DiaryScaffold
import java.util.Random

@Composable
fun DiarySummaryScreen(
    navController: NavController,
    viewModel: AiSummaryViewModel = viewModel()
) {
    // 로딩 상태 관리 뷰모델
    val uiState by viewModel.uiState.collectAsState()

    DiaryScaffold(
        navController = navController,
        title = "지금의 나"
    ) { innerPadding ->
        // 로딩일 경우 오버레이 씌우기
        if (uiState is SummaryUiState.Loading) {
            LoadingOverlay()
        }
        // 로딩이 아닐 경우
        else {
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // UI 상태에 따라 분기
                    when (val state = uiState) {

                        // 초기 상태 or 로딩 중일 경우
                        is SummaryUiState.Initial, SummaryUiState.Loading -> {
                            IntroContent(
                                onConfirmClick = { viewModel.getAiSummary() }
                            )
                        }

                        // 결과가 나왔을 경우
                        is SummaryUiState.Success -> {
                            ResultContent(summary = state.summary)
                        }

                        // 에러가 났을 경우
                        is SummaryUiState.Error -> {
                            ErrorContent(state.message)
                        }
                    }
                }
            }
        }
    }
}

// 초기 화면
@Composable
fun IntroContent(onConfirmClick: () -> Unit) {
    val randomMent = rememberSaveable {
        val nickname = UserSession.nickname
        listOf(
            "그동안 차곡차곡 쌓인 일기들을 읽고,\n요즘 ${nickname}님의 마음 날씨를 알려드릴게요.",
            "${nickname}님이 들려준 이야기들을 모아서,\n지금 가장 필요한 말을 준비했어요.",
            "지난 기록들에 담긴 감정들을 모아\n${nickname}님의 마음 흐름을 천천히 들여다볼게요.",
            "오늘따라 ${nickname}님의 마음이 궁금하네요.\nAI 친구가 일기장을 꼼꼼히 읽어봤어요."
        ).random()
    }

    Spacer(modifier = Modifier.height(48.dp))

    // 반짝이는 아이콘
    Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = null,
        modifier = Modifier.size(72.dp),
        tint = Color(0xFF006488)
    )

    Spacer(modifier = Modifier.height(48.dp))
    
    // 메인 텍스트
    Text(
        text = randomMent,
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 서브 안내 문구
    Text(
        text = "* 최근 90일간 작성된 일기 중 최대 30개를 살펴봅니다.",
        textAlign = TextAlign.Center,
        fontSize = 11.sp,
        color = Color.Gray,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(80.dp))

    // 로딩으로 전환 후 AI 종합 코멘트 API 호출
    Button(
        onClick = onConfirmClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(280.dp)
            .height(50.dp)
    ) {
        Text("지금의 내 마음 확인하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(40.dp))
}

// 결과 화면
@Composable
fun ResultContent(summary: AiSummaryResponse) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = summary.content,
                modifier = Modifier.padding(20.dp),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}

// 에러 화면
@Composable
fun ErrorContent(message: String) {
    Text(
        text = message,
        modifier = Modifier.padding(20.dp),
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
}

// 로딩 오버레이
@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF006488))
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = "AI 친구가 일기들을 확인하고 있어요..."
            )
        }
    }
}

