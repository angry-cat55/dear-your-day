package com.example.dearyourday.ui.screens.diary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.ui.components.DiaryScaffold

@Composable
fun MonthlyDiariesScreen(navController: NavController) {
    DiaryScaffold(
        navController = navController,
        title = "2026.01.26"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("일기 모아보기 (캘린더)", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text("달력이 들어갈 자리입니다.")

            Button(onClick = { navController.popBackStack() }) {
                Text("뒤로 가기")
            }
        }
    }
}