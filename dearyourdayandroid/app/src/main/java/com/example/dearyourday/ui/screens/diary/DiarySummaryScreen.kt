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
fun DiarySummaryScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("종합 공감 (분석)", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text("AI가 분석한 통계가 들어갑니다.")

            Button(onClick = { navController.popBackStack() }) {
                Text("뒤로 가기")
            }
        }
    }
}