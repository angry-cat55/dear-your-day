package com.example.dearyourday.ui.screens.diary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainDiaryScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text("메인 화면 (오늘의 일기)", fontSize = 24.sp)

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