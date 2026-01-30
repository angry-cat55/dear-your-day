package com.example.dearyourday.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.ui.components.SignupScaffold

@Composable
fun SignUpStep2Screen(navController: NavController) {
    SignupScaffold(
        navController = navController,
        title = "회원가입"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("회원가입 2단계: 전화번호", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(value = "", onValueChange = {}, label = { Text("전화번호") })

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate("signup_step3") }) {
                Text("다음 (닉네임 설정)")
            }
        }
    }
}