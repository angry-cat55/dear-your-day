package com.example.dearyourday.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.R
import com.example.dearyourday.data.AutoLoginManager
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.*
import com.example.dearyourday.data.model.user.*
import com.example.dearyourday.ui.screens.user.UserScreen
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun LoginScreen(navController: NavController) {
    // 화면에서 사용할 변수들 (상태)
    var loginId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current
    // suspend 함수 사용을 위한 객체
    val coroutineScope = rememberCoroutineScope()

    // 자동 로그인 체크 여부 저장 변수
    var isAutoLoginChecked by rememberSaveable { mutableStateOf(false) }
    // 저장소 매니저
    val autoLoginManager = remember { AutoLoginManager(context) }

    // 포커스 제어용 입력창의 변수들
    val idFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    // 포커스를 해제(키보드 내리기)할 때 사용하는 매니저
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .pointerInput(Unit) { // 빈 공간을 터치하면 키보드를 제거
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), // 키보드 버튼을 '다음'으로 변경
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() } // '다음'을 눌렀을 때 비밀번호 입력창으로 포커스 이동
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .focusRequester(idFocusRequester) // 아이디 입력창 주소 부착
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // 키보드 버튼을 '완료'로 사용
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() } // '완료'를 누르면 포커스 해제
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .focusRequester(passwordFocusRequester) // 비밀번호 입력창 주소 부착
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
                    checked = isAutoLoginChecked,
                    onCheckedChange = { isAutoLoginChecked = it }
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

                                // 성공 시 UserSession에 유저 정보 저장
                                UserSession.userId = result.userId
                                UserSession.nickname = result.nickname
                                UserSession.loginId = result.loginId
                                UserSession.email = result.email
                                UserSession.createdAt = result.createdAt

                                // 자동 로그인 체크박스 활성화 시 DataStore에 정보 저장
                                if (isAutoLoginChecked) {
                                    autoLoginManager.saveLoginData(loginId, password)
                                }
                                // 자동로그인 체크박스 해제했으면 기존 정보 삭제
                                else {
                                    autoLoginManager.clearLoginData()
                                }

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