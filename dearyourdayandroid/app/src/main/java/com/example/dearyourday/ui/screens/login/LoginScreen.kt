package com.example.dearyourday.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.*
import com.example.dearyourday.data.model.user.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    // 화면에서 사용할 변수들 (상태)
    var loginId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 제목
            Text(
                text = "Dear Your Day",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 아이디
            OutlinedTextField(
                value = loginId,
                onValueChange = { loginId = it },
                label = { Text("아이디") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                visualTransformation = PasswordVisualTransformation(), // 비밀번호 가리기
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 로그인
            Button(
                onClick = {
                    // 비동기 로직을 위한 코루틴
                    coroutineScope.launch {
                        try {
                            // 1. 보낼 데이터 포장 (DTO)
                            val request = LoginRequest(loginId = loginId, password = password)

                            // 2. 서버로 전송
                            val response = RetrofitInstance.userApi.login(request)
                            val result = response.body()

                            // 3. 결과 확인
                            if (response.isSuccessful && result != null) {

                                // 성공 시 처리 (일단 토스트 메시지만)
                                Toast.makeText(context, "로그인 성공! 환영합니다 ${result?.nickname ?: "사용자"}님", Toast.LENGTH_LONG).show()
                                UserSession.userId = result.userId
                                UserSession.nickname = result.nickname

                                // 화면 이동
                                navController.navigate("main_diary") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                // 실패 (비번 틀림 등)
                                Toast.makeText(context, "로그인 실패: 아이디나 비번을 확인하세요.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            // 에러 (인터넷 끊김, 서버 꺼짐 등)
                            e.printStackTrace()
                            Toast.makeText(context, "에러 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = "로그인")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 회원가입으로 이동
            TextButton(onClick = {
                navController.navigate("signup_step1")
            }) {
                Text("계정이 없으신가요? 회원가입")
            }
        }
    }
}