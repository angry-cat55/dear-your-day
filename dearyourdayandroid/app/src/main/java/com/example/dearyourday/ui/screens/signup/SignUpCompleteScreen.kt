package com.example.dearyourday.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.model.SignUpViewModel
import com.example.dearyourday.ui.components.SignupContentLayout
import com.example.dearyourday.ui.components.SignupScaffold

@Composable
fun SignUpCompleteScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val nickname = remember { viewModel.uiState.value.nickname }

    SignupScaffold(
        navController = navController,
        title = "",
        showBackButton = false // 뒤로가기 버튼 숨김 처리
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            SignupContentLayout(
                title = "회원가입 완료!\n환영합니다, ${nickname}님.",
                buttonText = "로그인하러 가기",
                onButtonClick = {
                    // 로그인 화면으로 이동 & 스택 비우기
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                    viewModel.clearData()
                }
            ) { }
        }
    }
}