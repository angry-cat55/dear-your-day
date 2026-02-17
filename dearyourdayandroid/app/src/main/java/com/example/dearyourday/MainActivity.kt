package com.example.dearyourday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.dearyourday.data.AutoLoginManager
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.user.LoginRequest
import com.example.dearyourday.navigation.NavGraph
import com.example.dearyourday.ui.theme.DearYourDayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    // 스플래시 화면을 계속 띄워둘지 결정하는 변수
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        var startDestination by mutableStateOf<String?>(null)

        lifecycleScope.launch {
            val autoLoginManager = AutoLoginManager(this@MainActivity)
            val minDelay = 1500L
            val startTime = System.currentTimeMillis()

            val savedData = autoLoginManager.loginDataFlow.firstOrNull()
            var loginSuccess = false

            if (savedData != null) {
                val (savedId, savedPw) = savedData
                try {
                    val response = RetrofitInstance.userApi.login(LoginRequest(savedId, savedPw))
                    val result = response.body()

                    // 자동 로그인 정보를 가져왔을 경우
                    if (response.isSuccessful && result != null) {
                        UserSession.userId = result.userId
                        UserSession.nickname = result.nickname
                        UserSession.loginId = result.loginId
                        UserSession.email = result.email
                        UserSession.createdAt = result.createdAt
                        loginSuccess = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 최소 대기 시간 보장
            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime < minDelay) {
                delay(minDelay - elapsedTime)
            }

            // 로직 결과에 따라 첫 화면 결정
            startDestination = if (loginSuccess) {
                val today = LocalDate.now().toString()
                "main_diary/$today"
            } else {
                "login"
            }

            // 스플래시 화면 닫기
            keepSplashScreen = false
        }

        enableEdgeToEdge()
        setContent {
            DearYourDayTheme {
                startDestination?.let { destination ->
                    // 결정된 시작 화면을 NavHost로 전달
                    NavGraph(startDestination = destination)
                }
            }
        }
    }
}