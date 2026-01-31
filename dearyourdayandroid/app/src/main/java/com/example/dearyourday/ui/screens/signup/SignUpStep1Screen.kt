package com.example.dearyourday.ui.screens.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.model.SignUpViewModel
import com.example.dearyourday.data.model.diary.DiaryResponse
import com.example.dearyourday.ui.components.SignupContentLayout
import com.example.dearyourday.ui.components.SignupScaffold

@Composable
fun SignUpStep1Screen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    // 아이디
    var loginId by rememberSaveable { mutableStateOf("") }
    // 비밀번호
    var password by rememberSaveable { mutableStateOf("") }
    // 비밀번호 확인
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current
    // suspend 함수 사용을 위한 객체 (아이디 중복 검사)
    val coroutineScope = rememberCoroutineScope()

    SignupScaffold(
        navController = navController,
        title = "회원가입 (1/3)"
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SignupContentLayout(
                title = "아이디와 비밀번호를\n입력해주세요.",
                onButtonClick = {
                    checkAndNavigateToNext(
                        viewModel = viewModel,
                        context = context,
                        navController = navController,
                        loginId = loginId,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                }
            ) {
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
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 비밀번호
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6A5AE0),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 확인
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("비밀번호 확인") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6A5AE0),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

// 다음 버튼 onClick 메소드
private fun checkAndNavigateToNext(
    viewModel: SignUpViewModel,
    context: Context,
    navController: NavController,
    loginId: String,
    password: String,
    confirmPassword: String
) {
    // 1. 빈 값 체크
    if (loginId.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
        Toast.makeText(context, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    // 2. 비밀번호 일치 체크
    if (password != confirmPassword) {
        Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        return
    }

    // 3. 비밀번호 길이 체크 (4자리 이상)
    if (password.length < 4) {
        Toast.makeText(context, "비밀번호는 4자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
        return
    }

    // 4. 모든 검사 통과 -> 뷰모델에 저장
    viewModel.updateIdPassword(loginId, password)

    // 5. 다음 화면으로 이동
    navController.navigate("signup_step2")
}