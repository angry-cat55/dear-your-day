package com.example.dearyourday.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// 전화번호 포맷팅을 위한 클래스
class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // 입력된 순수 숫자 텍스트(01012345678)
        val trimmed = if (text.text.length >= 11) text.text.substring(0, 11) else text.text
        // 포맷팅되는 숫자 저장 변수
        var out = ""

        // 하이픈(-)을 넣는 로직 (010-1234-5678 형식)
        for (i in trimmed.indices) {
            // 숫자 추가
            out += trimmed[i]
            // 지금 넣은 숫자가 3번째거나 7번째면, 뒤에 하이픈 추가
            if (i == 2 || i == 6) out += "-"
        }
        
        // 커서 위치 조정
        val numberOffsetTranslator = object : OffsetMapping {

            // 실제 데이터 커서 -> 화면(하이픈 포함) 커서 위치 계산
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 6) return offset + 1
                if (offset <= 11) return offset + 2
                return 13 // 최대 길이
            }

            // 화면(하이픈 포함) 커서 -> 실제 데이터 커서 위치 계산
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 12) return offset - 2
                return 11 // 최대 길이
            }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}