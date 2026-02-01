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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.dearyourday.data.model.SignUpViewModel
import com.example.dearyourday.ui.components.SignupContentLayout
import com.example.dearyourday.ui.components.SignupScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun SignUpStep3Screen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    // 닉네임
    var nickname by rememberSaveable { mutableStateOf("") }

    // 로딩 상태 관리 변수
    var isLoading by remember { mutableStateOf(false) }
    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current
    // suspend 함수 사용을 위한 객체 (계정 정보 저장)
    val coroutineScope = rememberCoroutineScope()

    SignupScaffold(
        navController = navController,
        title = "회원가입 (3/3)",
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SignupContentLayout(
                title = "닉네임을\n입력해주세요.",
                buttonText = "가입",
                onButtonClick = {
                    checkAndNavigateToNext(
                        viewModel = viewModel,
                        context = context,
                        navController = navController,
                        nickname = nickname,
                        coroutineScope = coroutineScope,
                        onLoadingChange = { isLoading = it }
                    )
                }
            ) {
                // 닉네임
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("닉네임") },
                    singleLine = true,
                    enabled = !isLoading,
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

    // 로딩 화면 (isLoading일 때만 덮어씌움)
    if (isLoading) {
        // 터치 입력을 막는 투명/반투명 배경
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            color = Color.Black.copy(alpha = 0.5f), // 반투명 검정
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

// 가입 버튼 onClick 메소드
private fun checkAndNavigateToNext(
    viewModel: SignUpViewModel,
    context: Context,
    navController: NavController,
    nickname: String,
    coroutineScope: CoroutineScope,
    onLoadingChange: (Boolean) -> Unit
) {
    // 1. 닉네임 빈 값 체크
    if (nickname.isBlank()) {
        Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    // 2. 뷰모델에 닉네임 저장
    viewModel.updateNickname(nickname)

    // 3. 코루틴 시작
    coroutineScope.launch {
        // 4. 로딩 시작
        onLoadingChange(true)

        // 5. 뷰모델 데이터 DB에 저장하는 API 메소드 호출 후 결과 저장
        val response = viewModel.requestSignUp()

        // 6. 로딩 끝
        onLoadingChange(false)

        // 7. 저장에 성공할 경우 화면 이동
        if (response.isSuccessful) {
            navController.navigate("signup_complete") {
                popUpTo("login") { inclusive = false }
            }
        }
        // 8. 실패할 경우 토스트로 오류 메세지 띄우기
        else {
            val errorMsg = try {
                val errorBody = response.errorBody()?.string()
                JSONObject(errorBody).getString("message")
            } catch (e: Exception) {
                "알 수 없는 오류"
            }
            Toast.makeText(context, "가입 실패: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }
}