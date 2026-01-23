package com.example.dearyourday.ui.screens

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
import com.example.dearyourday.data.api.*
import com.example.dearyourday.data.model.diary.*
import com.example.dearyourday.data.model.user.*
import com.example.dearyourday.data.model.aisummary.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen() {
    // 1. 화면에서 사용할 변수들 (상태)
    var loginId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding -> // innerPadding: 상태바, 하단바 등을 제외한 '안전한 영역'의 간격

        // 2. 화면 배치 (Column: 세로로 쌓기)
        Column(
            modifier = Modifier
                .fillMaxSize() // 화면 전체 채우기
                .padding(innerPadding)
                .padding(16.dp), // 테두리 여백
            horizontalAlignment = Alignment.CenterHorizontally, // 가로 가운데 정렬
            verticalArrangement = Arrangement.Center // 세로 가운데 정렬
        ) {
            // [제목]
            Text(
                text = "Dear Your Day",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // [입력 1] 아이디
            OutlinedTextField(
                value = loginId,
                onValueChange = { loginId = it }, // 글자 칠 때마다 변수에 저장
                label = { Text("아이디") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp)) // 여백

            // [입력 2] 비밀번호
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                visualTransformation = PasswordVisualTransformation(), // 비밀번호 가리기 (●●●●)
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // [버튼] 로그인
            Button(
                onClick = {
                    // [추가 3] 버튼 누르면 코루틴 실행!
                    coroutineScope.launch {
                        try {
                            // 1. 보낼 데이터 포장 (DTO)
                            val request = LoginRequest(loginId = loginId, password = password)

                            // 2. 서버로 전송! (Retrofit 엔진 사용)
                            // 아까 만든 userApi.login 함수 호출
                            val response = RetrofitInstance.userApi.login(request)

                            // 3. 결과 확인
                            if (response.isSuccessful) {
                                val result = response.body()
                                // 성공 시 처리 (일단 토스트 메시지만)
                                Toast.makeText(context, "로그인 성공! 환영합니다 ${result?.nickname}님", Toast.LENGTH_LONG).show()

                                // TODO: 나중에 메인 화면으로 이동하는 코드 넣을 곳
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

            // [버튼] 회원가입으로 이동 (텍스트 버튼)
            TextButton(onClick = { /* TODO: 회원가입 화면 이동 */ }) {
                Text("계정이 없으신가요? 회원가입")
            }
        }
    }
}