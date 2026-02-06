package com.example.dearyourday.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dearyourday.R
import com.example.dearyourday.data.AutoLoginManager
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.user.LoginRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

@Composable
// 앱 실행 시 표시되면 로딩 화면 (자동 로그인 시도)
fun SplashScreen(navController: NavController) {
    // 자동 로그인 해제를 위한 매니저
    val context = LocalContext.current
    val autoLoginManager = remember { AutoLoginManager(context) }

    LaunchedEffect(Unit) {
        // 1. 최소 대기 시간
        val minDelay = 1500L
        val startTime = System.currentTimeMillis()

        // 2. 저장된 정보 가져오기
        val savedData = autoLoginManager.loginDataFlow.firstOrNull()

        // 자동 로그인 성공 여부
        var loginSuccess = false

        if (savedData != null) {
            val (savedId, savedPw) = savedData
            try {
                // 3. 자동 로그인 시도
                val response = RetrofitInstance.userApi.login(LoginRequest(savedId, savedPw))
                val result = response.body()

                if (response.isSuccessful && result != null) {
                    UserSession.userId = result.userId
                    UserSession.nickname = result.nickname
                    loginSuccess = true
                }
            }
            // 자동 로그인 실패 시 사용자가 직접 입력하도록 하기
            catch (e: Exception) {
                // 오류 리포트 출력
                e.printStackTrace()
            }
        }

        // 로고 보여주는 최소 시간 보장
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime
        if (elapsedTime < minDelay) {
            delay(minDelay - elapsedTime)
        }

        // 4. 화면 이동 (분기 처리)
        // 자동 로그인 성공
        if (loginSuccess) {
            val today = LocalDate.now().toString()
            navController.navigate("main_diary/$today") {
                popUpTo("splash") { inclusive = true }
            }
        }
        // 자동 로그인 실패
        else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // 로고 하나
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_logo_transparent), // 로고 이미지
            contentDescription = "메인 로고",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            contentScale = ContentScale.Fit
        )
    }
}