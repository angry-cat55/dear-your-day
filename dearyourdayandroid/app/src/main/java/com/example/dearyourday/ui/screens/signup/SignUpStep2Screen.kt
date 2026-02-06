package com.example.dearyourday.ui.screens.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.SignUpViewModel
import com.example.dearyourday.data.model.user.SendEmailRequest
import com.example.dearyourday.data.model.user.VerifyEmailResponse
import com.example.dearyourday.ui.components.SignupContentLayout
import com.example.dearyourday.ui.components.SignupScaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpStep2Screen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    // 이메일
    var email by rememberSaveable { mutableStateOf("") }
    // 입력한 인증번호
    var inputAuthCode by rememberSaveable { mutableStateOf("") }
    // 발급된 인증번호
    val authCode by rememberSaveable { mutableStateOf("123456") } // 하드코딩
    // 인증번호 전송 여부 (true: 인증번호 발송 완료)
    var isEmailSent by rememberSaveable { mutableStateOf(false) }
    // 인증번호 확인 여부 (true: 인증 완료)
    var isEmailVerified by rememberSaveable { mutableStateOf(false) }

    // 남은 시간 저장 변수 (180초)
    var timeLeft by remember { mutableIntStateOf(180) }

    // 타이머를 강제 재시작을 위한 변수 (재전송 버튼 누를 때 증가)
    var timerKey by remember { mutableIntStateOf(0) }

    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current
    // suspend 함수 사용을 위한 객체 (이메일 인증)
    val coroutineScope = rememberCoroutineScope()

    // 타이머 로직 (isEmailSent가 true가 되거나, 재전송 변수가 바뀌면 실행)
    LaunchedEffect(isEmailSent, timerKey) {
        if (isEmailSent) {
            timeLeft = 180 // 시간 초기화
            while (timeLeft > 0 && !isEmailVerified) {
                delay(1000L) // 1초 대기
                timeLeft--   // 1초 감소
            }
        }
    }

    SignupScaffold(
        navController = navController,
        title = "회원가입 (2/3)"
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SignupContentLayout(
                title = "이메일을\n입력해주세요.",
                onButtonClick = {
                    checkAndNavigateToNext(
                        viewModel = viewModel,
                        context = context,
                        navController = navController,
                        email = email,
                        isEmailVerified = isEmailVerified
                    )
                }
            ) {
                // 이메일 + 인증번호 발송 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min), // 입력창 높이에 맞춤
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 이메일
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            isEmailSent = false
                            isEmailVerified = false
                            email = it
                        },
                        label = { Text("이메일") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done), // 이메일 키보드 설정
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6A5AE0),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        modifier = Modifier
                            .weight(1f) // 버튼 공간 빼고 나머지
                            .fillMaxHeight()
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // 입력창과 버튼 사이 간격

                    // 인증번호 발송 버튼
                    Button(
                        onClick = {
                            // 이메일 비어있는지 체크
                            if (email.isBlank()) {
                                Toast.makeText(context, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // 인증번호 입력칸 + 타이머 활성화
                            isEmailSent = true

                            coroutineScope.launch {
                                // 이메일 인증번호 전송 후 결과 저장
                                val response = RetrofitInstance.userApi.sendEmail(SendEmailRequest(email = email))

                                // 이메일 전송에 실패할 경우
                                if (!response.isSuccessful) {
                                    // 인증번호 입력칸 비활성화 + 타이머 초기화
                                    isEmailSent = false;
                                    Toast.makeText(context, "인증번호 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isEmailVerified,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A5AE0)
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(110.dp)
                            .padding(top = 8.dp) // OutlinedTextField와 동일한 높이로 보이게 설정
                    ) {
                        Text(
                            text = "인증번호 받기",
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 인증번호 + 인증번호 확인 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min), // 입력창 높이에 맞춤
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 인증번호
                    OutlinedTextField(
                        value = inputAuthCode,
                        onValueChange = { newValue ->
                            // 최대 6자리까지 입력 + 숫자만 입력 가능
                            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                                inputAuthCode = newValue
                            }
                        },
                        enabled = isEmailSent,
                        label = { Text("인증번호") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // 숫자 키보드 설정
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6A5AE0),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        trailingIcon = { // 입력창 우측에 타이머 표시
                            if (isEmailSent && !isEmailVerified) {
                                val minutes = timeLeft / 60
                                val seconds = timeLeft % 60
                                Text(
                                    text = "%02d:%02d".format(minutes, seconds), // '분:초' 로 포맷팅
                                    color = Color(0xFFFF6B6B),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f) // 버튼 공간 빼고 나머지
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // 입력창과 버튼 사이 간격

                    // 인증번호 확인 버튼
                    Button(
                        onClick = {
                            // 인증번호 비어있는지 체크
                            if (inputAuthCode.isBlank()) {
                                Toast.makeText(context, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // 인증번호 길이 체크
                            if (inputAuthCode.length < 6) {
                                Toast.makeText(context, "인증번호 6자리를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            coroutineScope.launch {
                                // 이메일과 인증번호 전송 후 결과 저장
                                val response = RetrofitInstance.userApi.verifyEmail(
                                    VerifyEmailResponse(
                                        email = email,
                                        authCode = inputAuthCode
                                    ))

                                val isCorrect = response.body()

                                // 1. 인증번호 일치 확인에 성공할 경우
                                if (response.isSuccessful) {
                                    // 인증번호 일치할 경우
                                    if (isCorrect == true) {
                                        isEmailVerified = true
                                        isEmailSent = false;
                                        Toast.makeText(context,"인증번호 인증을 성공했습니다.",Toast.LENGTH_SHORT).show()
                                    }
                                    // 인증번호가 일치하지 않을 경우
                                    else {
                                        Toast.makeText(context,"인증번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                                    }
                                }
                                // 2. 중복 확인에 실패할 경우
                                else {
                                    Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = isEmailSent,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A5AE0)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(110.dp)
                            .padding(top = 8.dp) // OutlinedTextField와 동일한 높이로 보이게 설정
                    ) {
                        Text(
                            text = "인증하기",
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }

                // 재전송 텍스트 버튼
                if (isEmailSent && !isEmailVerified) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 아이콘 + 안내 텍스트
                        Text(
                            text = "ⓘ 이메일을 받지 못하셨나요? ",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        // 재전송 버튼 (밑줄 텍스트)
                        Text(
                            text = "이메일 재전송하기",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline, // 밑줄
                            modifier = Modifier.clickable {
                                // 1. 타이머 재시작
                                timerKey++

                                // 2. 이메일 발송 API 다시 호출
                                coroutineScope.launch {
                                    val response = RetrofitInstance.userApi.sendEmail(SendEmailRequest(email = email))
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "인증번호를 재전송했습니다.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "재전송 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// 다음 버튼 onClick 메소드
private fun checkAndNavigateToNext(
    viewModel: SignUpViewModel,
    context: Context,
    navController: NavController,
    email: String,
    isEmailVerified: Boolean
) {
    // 1. 인증 여부 확인
    if (!isEmailVerified) {
        Toast.makeText(context, "이메일을 인증해주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    // 2. 이메일 검증 통과 -> 뷰모델에 이메일 저장
    viewModel.updateEmail(email)

    // 3. 다음 화면으로 이동
    navController.navigate("signup_step3")
}