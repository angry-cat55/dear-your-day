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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.model.SignUpViewModel
import com.example.dearyourday.ui.components.SignupContentLayout
import com.example.dearyourday.ui.components.SignupScaffold

@Composable
fun SignUpStep2Screen(
    navController: NavController,
    viewModel: SignUpViewModel
) {

    // 전화번호
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    // 입력한 인증번호
    var inputAuthCode by rememberSaveable { mutableStateOf("") }
    // 발급된 인증번호
    val authCode by rememberSaveable { mutableStateOf("123456") }

    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current
    // suspend 함수 사용을 위한 객체 (전화번호 인증)
    val coroutineScope = rememberCoroutineScope()

    SignupScaffold(
        navController = navController,
        title = "회원가입"
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SignupContentLayout(
                title = "전화번호를\n입력해주세요.",
                onButtonClick = {
                    checkAndNavigateToNext(
                        viewModel = viewModel,
                        context = context,
                        navController = navController,
                        phoneNumber = phoneNumber,
                        inputAuthCode = inputAuthCode,
                        authCode = authCode
                    )
                }
            ) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("전화번호") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6A5AE0),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputAuthCode,
                    onValueChange = { inputAuthCode = it},
                    label = { Text("인증번호") },
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

private fun checkAndNavigateToNext(
    viewModel: SignUpViewModel,
    context: Context,
    navController: NavController,
    phoneNumber: String,
    inputAuthCode: String,
    authCode: String
) {
    // 1. 전화번호 빈 값 체크
    if (phoneNumber.isBlank()) {
        Toast.makeText(context, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    // 2. 인증번호 입력란 빈 값 체크
    if (inputAuthCode.isBlank()) {
        Toast.makeText(context, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    // 3. 인증번호 발송 여부 체크 (혹시 인증번호 요청도 안 하고 확인을 눌렀을 때)
    if (authCode.isBlank()) {
        Toast.makeText(context, "인증번호 받기 버튼을 먼저 눌러주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    // 4. 인증번호 일치 체크
    if (inputAuthCode != authCode) {
        Toast.makeText(context, "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        return
    }

    // 5. 모든 검증 통과 -> 뷰모델에 전화번호 저장
    viewModel.updatePhoneNumber(phoneNumber)

    // 6. 다음 화면으로 이동
    navController.navigate("signup_step3")
}