package com.example.dearyourdayserver.service;

import com.example.dearyourdayserver.dto.gemini.GeminiRequest;
import com.example.dearyourdayserver.dto.gemini.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // 로그 남기기 위해 사용
public class GeminiService {

    private final RestClient geminiRestClient;
    private static final String GEMINI_MODEL_PATH = "/v1beta/models/gemma-3-4b-it:generateContent";

    // 일기 하나에 대한 공감 코멘트
    public String getCoachingComment(String diaryContent) {
        String prompt =
            """
            당신은 사용자의 일기를 읽고 다정하게 공감해 주는 'AI 심리 상담가'이자 '가장 친한 친구'입니다.
            다음 원칙을 반드시 지켜주세요:
            1. 일기 내용을 요약하지 말고, 사용자가 느꼈을 '감정'에 집중해서 공감해 주세요.
            2. 말투는 부드럽고 따뜻한 한국어 존댓말(해요체)을 사용하세요.
            3. 길이는 100자 내외로 짧고 간결하게 작성하세요.
            4. 너무 가르치려 들거나 기계적인 답변(예: "힘내세요")은 피하고, 진심 어린 위로를 건네주세요.
            
            [일기 내용]
            %s
            """.formatted(diaryContent);

        return callGeminiApi(prompt);
    }

    //  90일치 일기 종합 공감
    public String getSummary(List<String> diaryContents) {
        // 일기 내용들을 하나의 문자열로 합침 (구분선 사용)
        String joinedDiaries = String.join("\n\n---\n\n", diaryContents);

        String prompt =
            """
            당신은 사용자의 지난 90일간의 일기 기록을 분석하여 삶의 패턴과 감정을 통찰하는 '전문 라이프 코치'입니다.
            제공된 일기 목록을 바탕으로 아래 3가지 항목을 포함한 '종합 공감 리포트'를 작성해 주세요.
            
            [작성 요구사항]
            1. 감정의 흐름: 특정 날짜나 사건을 나열하지 말고, 전체적으로 사용자의 마음이 어떤 날씨였는지(예: "초반의 불안감이 점차 설렘으로 바뀌었군요") 비유적으로 서술해 주세요.
            2. 마음의 중심: 단순히 많이 쓴 단어를 찾는 게 아니라, 사용자가 진정으로 중요하게 생각하는 가치나 현재 몰입하고 있는 것이 무엇인지(예: "성장에 대한 열망이 느껴집니다") 깊이 있게 짚어주세요.
            3. 따뜻한 응원: 분석한 내용을 토대로, 친한 친구가 어깨를 토닥여주듯 따뜻하고 구체적인 응원의 말을 건네주세요.
            
            [제약 사항]
            - 절대로 "1월 20일에는...", "21일에는..." 처럼 특정 날짜를 언급하거나 사건을 나열하지 마세요.
            - "'코딩', '테스트' 같은 키워드가 등장했습니다" 식의 기계적인 분석 문장을 쓰지 마세요.
            - 말투는 부드러운 존댓말(해요체)로, 전체 분량은 공백 포함 400~500자 내외로 작성해 주세요.
            - 마크다운(Markdown) 문법을 사용하지 마세요. (** 등)
            
            [분석 대상 일기 목록]
            %s
            """.formatted(joinedDiaries);

        return callGeminiApi(prompt);
    }

    // 내부 메서드: 실제 API 호출 (중복 제거)
    private String callGeminiApi(String prompt) {
        GeminiRequest request = GeminiRequest.of(prompt);

        GeminiResponse response = geminiRestClient.post()
                .uri(GEMINI_MODEL_PATH)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

        // 응답은 왔는데 내용이 비어있는 경우 처리
        if (response == null || response.getText() == null) {
            throw new RuntimeException("Gemini API 응답이 비어있습니다.");
        }

        return response.getText();
    }
}