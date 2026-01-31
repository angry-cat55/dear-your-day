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
        title = "회원가입"
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SignupContentLayout(
                title = "닉네임을\n입력해주세요.",
                onButtonClick = {
                    checkAndNavigateToNext(
                        viewModel = viewModel,
                        context = context,
                        navController = navController,
                        nickname = nickname,
                        coroutineScope = coroutineScope,
                        // ★ 로딩 상태를 바꾸는 함수를 전달
                        onLoadingChange = { isLoading = it }
                    )
                }
            ) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("닉네임") },
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

private fun checkAndNavigateToNext(
    viewModel: SignUpViewModel,
    context: Context,
    navController: NavController,
    nickname: String,
    coroutineScope: CoroutineScope,
    onLoadingChange: (Boolean) -> Unit // ★ 로딩 상태 변경 콜백 추가
) {
    if (nickname.isBlank()) {
        Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    viewModel.updateNickname(nickname)

    coroutineScope.launch {
        // 1. 로딩 시작! (화면이 어두워짐)
        onLoadingChange(true)

        // 2. 서버 통신 (여기서 대기)
        val response = viewModel.requestSignUp()

        // 3. 로딩 끝! (화면이 다시 밝아짐)
        onLoadingChange(false)

        if (response.isSuccessful) {
            // 성공: 완료 화면으로 이동 & 뒤로가기 스택 날리기
            navController.navigate("signup_complete") {
                popUpTo("login") { inclusive = false }
            }
        } else {
            // 실패: 이동 안 함. 토스트만 띄움.
            // 사용자는 로딩만 걷힌 상태에서 바로 닉네임을 수정할 수 있음 (UX Good!)
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