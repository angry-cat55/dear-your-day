package com.example.dearyourday.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.R
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.*
import com.example.dearyourday.data.model.user.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun LoginScreen(navController: NavController) {
    // 화면에서 사용할 변수들 (상태)
    var loginId by rememberSaveable { mutableStateOf("root") }
    var password by rememberSaveable { mutableStateOf("1234") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // TODO: 로컬 스토리지 등을 통해 자동 로그인 로직 변경 (일단 하드코딩)
    var checked by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(128.dp))

            // 로고 이미지
            Image(
                painter = painterResource(id = R.drawable.main_logo_transparent),
                contentDescription = "로고 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentScale = ContentScale.Fit
            )

            // 아이디
            OutlinedTextField(
                value = loginId,
                onValueChange = { loginId = it },
                label = { Text("아이디") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6A5AE0),
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                visualTransformation = PasswordVisualTransformation(), // 비밀번호 가리기
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6A5AE0),
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 자동 로그인 체크박스 + 텍스트
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Text("자동 로그인", fontSize = 14.sp)
            }


            Spacer(modifier = Modifier.height(30.dp))

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

                                // 성공 시 처리 (테스트용 토스트 메시지)
                                // TODO: 개발 진도에 따라 해당 로직 삭제할 것
                                Toast.makeText(context, "로그인 성공! 환영합니다 ${result?.nickname ?: "사용자"}님", Toast.LENGTH_LONG).show()
                                UserSession.userId = result.userId
                                UserSession.nickname = result.nickname

                                // 화면 이동
                                val today = LocalDate.now().toString();
                                navController.navigate("main_diary/$today") {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp)
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