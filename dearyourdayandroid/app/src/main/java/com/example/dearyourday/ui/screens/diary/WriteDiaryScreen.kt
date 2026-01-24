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
fun WriteDiaryScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("일기 작성 화면", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = "", onValueChange = {},
                label = { Text("오늘 하루는 어땠나요?") },
                modifier = Modifier.height(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                // 저장 후 메인으로 복귀 (popBackStack)
                navController.popBackStack()
            }) {
                Text("저장하기")
            }
        }
    }
}