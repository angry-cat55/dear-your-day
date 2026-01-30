package com.example.dearyourday.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignupContentLayout(
    title: String, // 화면별 제목
    buttonText: String = "다음", // 버튼 텍스트
    isButtonEnabled: Boolean = true, // 버튼 활성화 여부
    onButtonClick: () -> Unit, // 버튼 눌렀을 때 동작
    content: @Composable () -> Unit // 입력창들
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp) // 좌우 배딩
            .padding(top = 80.dp, bottom = 180.dp), // 위아래 패딩
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 제목
        Text(
            text = title,
            fontSize = 24.sp,
            lineHeight = 34.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(50.dp))

        // 2. 입력창 영역
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }

        // 3. 남은 공간 차지 (아래쪽 150dp 제외)
        Spacer(modifier = Modifier.weight(1f))

        // 4. 버튼
        Button(
            onClick = onButtonClick,
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = buttonText)
        }
    }
}