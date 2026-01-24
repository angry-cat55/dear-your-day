package com.example.dearyourday.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignUpCompleteScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("회원가입 완료!", fontSize = 30.sp)
            Text("환영합니다, 닉네임님.")

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = {
                // 로그인 화면으로 돌아가기 (스택 비우기)
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }) {
                Text("로그인하러 가기")
            }
        }
    }
}