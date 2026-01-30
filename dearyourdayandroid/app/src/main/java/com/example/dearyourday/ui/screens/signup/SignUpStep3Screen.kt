package com.example.dearyourday.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.ui.components.SignupScaffold

@Composable
fun SignUpStep3Screen(navController: NavController) {
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
            Text("회원가입 3단계: 닉네임", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(value = "", onValueChange = {}, label = { Text("닉네임") })

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                navController.navigate("signup_complete")  {
                    popUpTo(0) { inclusive = true}
                }
            }) {
                Text("가입 완료하기")
            }
        }
    }
}