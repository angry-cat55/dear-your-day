package com.example.dearyourday.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dearyourday.data.AutoLoginManager
import com.example.dearyourday.data.UserSession
import com.example.dearyourday.data.api.RetrofitInstance
import com.example.dearyourday.data.model.user.NicknameUpdateRequest
import com.example.dearyourday.ui.components.DiaryScaffold
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    navController: NavController
) {
    // 닉네임 저장 변수
    var nickname by rememberSaveable { mutableStateOf(UserSession.nickname) }
    // 변경 전 닉네임 저장 변수
    var originalNickname by rememberSaveable { mutableStateOf(UserSession.nickname) }

    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current
    // 포커스 매니저
    val focusManager = LocalFocusManager.current
    // suspend 함수 사용을 위한 객체
    val coroutineScope = rememberCoroutineScope()
    // 수정된 닉네임 DataStore에 저장하기 위한 매니저
    val autoLoginManager = remember { AutoLoginManager(context) }


    DiaryScaffold(
        navController = navController,
        title = "내 계정"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 닉네임 입력창
            InputFieldSection(
                label = "닉네임",
                value = nickname,
                onValueChange = { nickname = it },
                isEditable = true, // 수정 가능
                onClearClick = { nickname = "" }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 읽기 전용 정보들
            // 로그인 아이디
            InputFieldSection(
                label = "로그인 아이디",
                value = UserSession.loginId,
                isEditable = false
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 이메일
            InputFieldSection(
                label = "이메일",
                value = UserSession.email,
                isEditable = false
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 계정 생성 일자
            InputFieldSection(
                label = "계정 생성 일자",
                value = (UserSession.createdAt) // 날짜 포맷팅
                    .replace("T", " ")
                    .replace("-", "."),
                isEditable = false
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
            )

            // 닉네임 저장하기 버튼
            Button(
                onClick = {
                    // 1. 기존 닉네임과 동일할 경우
                    if (nickname == originalNickname) {
                        Toast.makeText(context, "기존 닉네임과 동일합니다.", Toast.LENGTH_SHORT).show()
                    }
                    // 2. 닉네임 입력 데이터가 비어있을 경우
                    else if (nickname.isBlank()) {
                        Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    // 3. 변경
                    else {
                        coroutineScope.launch {
                            try {
                                // DB 닉네임 수정 API 요청
                                val response = RetrofitInstance.userApi.updateNickname(
                                    userId = UserSession.userId,
                                    request = NicknameUpdateRequest(nickname)
                                )

                                // DB 닉네임 변경에 성공할 경우
                                if (response.isSuccessful) {
                                    val newNickname = response.body()

                                    // 기존 닉네임 상태 변경
                                    originalNickname = newNickname!!

                                    // DataStore와 UserSession에 닉네임 데이터 갱신
                                    autoLoginManager.updateNickname(newNickname)
                                    UserSession.nickname = newNickname

                                    Toast.makeText(
                                        context,
                                        "${newNickname}으로 변경했습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // 변경에 실패할 경우
                                else {
                                    Toast.makeText(context, "닉네임 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                // 예외 오류
                            } catch (e: Exception) {
                                Toast.makeText(context, "에러 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "저장하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 계정 탈퇴 텍스트 버튼
            Text(
                text = "회원 탈퇴하기",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    // TODO: 탈퇴 로직 구현
                }
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// 반복되는 입력창 컴포넌트
@Composable
fun InputFieldSection(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    isEditable: Boolean,
    onClearClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 라벨
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        // 입력창
        OutlinedTextField(
            value = value,
            onValueChange = if (isEditable) onValueChange else { _ -> },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditable, // 수정 가능 여부
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                // 수정 가능할 때: 흰색 배경, 수정 불가능할 때: 회색 배경
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF5F5F5),

                focusedBorderColor = Color(0xFF6A5AE0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                disabledBorderColor = Color.Transparent,

                disabledTextColor = Color.Gray
            ),
            // 닉네임 입력 데이터 지우기 버튼
            trailingIcon = if (isEditable && value.isNotEmpty()) {
                {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "닉네임 지우기",
                            tint = Color.Gray
                        )
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
    }
}