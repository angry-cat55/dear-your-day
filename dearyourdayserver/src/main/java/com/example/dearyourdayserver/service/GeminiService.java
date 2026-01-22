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
    public String getCoachingComment(String diaryContent, String userNickname) {
        String prompt =
            """
            당신은 사용자의 일기를 읽고 마음 깊이 공감해주는 '가장 친한 친구'입니다.
            상담가처럼 분석하거나 사용자의 하루를 해석하고 평가하거나 가르치려 하지 말고, 오직 그 '감정'에만 집중해서 사용자가 느끼는 감정을 조심스럽게 어루만지듯 반응하여 대답해주세요.
            
            [작성 요구사항]
            1. 길이는 150자 내외로 작성하세요.
            2. 말투는 다정하고 부드러운 한국어 존댓말 '해요체'를 쓰세요.
            
            [제약 사항(매우 중요)]
            - "오늘 ~하셨군요" 처럼 일기 내용을 기계적으로 다시 읊지 마세요.
            - 사용자의 닉네임을 반드시 사용할 필요는 없으며, 필요에 따라 사용하지 않아도 됩니다.
            - 사용자의 닉네임은 무조건 첫 문장에서 부를 필요없이 (예: "홍길동님", "와 홍길동님!"), 글의 중간 혹은 끝부분처럼 필요한 부분에서 자연스럽게 사용하세요.
            - 일기 내용을 요약하지 말고, 사용자가 느꼈을 '감정'에 집중해서 공감해 주세요.
            - 해결책이나 조언을 제시하지 말고, "저라면 마음이 많이 무거웠을 것 같아요", "그 감정이 쉽게 가라앉지 않았을 것 같아요"처럼 감정에 조용히 동조하세요.
            - "힘내세요", "응원합니다" 같은 기계적이고 상투적인 표현은 피하고, 진심 어린 공감을 건네주세요.
            - "이별하셔서 힘드시겠어요"처럼 원인과 결과를 논리적으로 연결해 설명하지 마세요. 이유를 설명하는 대신, 지금의 마음 상태에만 머물러 주세요.
            - 감정을 단정하거나 과도하게 비극화하지 말고, 차분하고 절제된 언어로 곁에 있어주는 느낌을 주세요.
            
            [사용자 닉네임]
            %s
            
            [일기 내용]
            %s
            """.formatted(userNickname, diaryContent);

        return callGeminiApi(prompt);
    }

    //  90일치 일기 종합 공감
    public String getSummary(List<String> diaryContents, String userNickname) {
        // 일기 내용들을 하나의 문자열로 합침 (구분선 사용)
        String joinedDiaries = String.join("\n\n---\n\n", diaryContents);

        String prompt =
            """
            당신은 사용자의 기록을 오래 곁에서 지켜본 '가장 가까운 친구'입니다.
            아래의 일기들은 분석 대상이 아니라, 친구의 삶을 자연스럽게 이해하기 위한 이야기들입니다.
            
            [사용자 닉네임]
            %s
            
            [일기 모음]
            %s
            
            [작성 요구사항]
            말투는 다정하고 부드러운 한국어 존댓말 '해요체'를 쓰고, 전체 분량은 공백 포함 400~500자 내외로 작성해 주세요.
            글에는 아래의 흐름이 자연스럽게 녹아 있어야 합니다.
            - 처음과 요즘을 대비하며, 사용자의 감정이나 태도가 어떻게 달라졌는지를 날짜나 사건 나열 없이, 분위기와 결의 변화 위주로 표현해 주세요.
            - 사용자가 반복적으로 붙잡고 있는 태도, 선택, 마음가짐을 통해 이 사람이 어떤 가치를 중요하게 여기는지 은근히 짚어 주세요.
            - 조언이나 평가가 아니라, 곁에 앉아 조용히 응원하는 말로 글을 마무리해 주세요.
            
            [제약 사항(매우 중요)]
            - 글은 반드시 사용자에게 직접 말을 건네는 문장으로 자연스럽게 시작하세요.
            - 절대로 제목, 소제목, 인사말, 선언적인 문장으로 시작하지 마세요.
            - 절대로 "안녕하세요", "일기를 읽어보았습니다" 같은 표현으로 시작하지 마세요.
            - "지난 90일간의 N개의 일기를 읽어보았어요." 같은 AI 분석 문장을 쓰지 마세요.
            - 마크다운(**, ## 등)을 절대 사용하지 마세요. 오직 순수 텍스트(Plain Text)로만 작성하세요.
            - "1월 20일에는...", "21일에는..." 처럼 특정 날짜나 사건을 나열하지 마세요.
            - 키워드 빈도나 분석 결과를 설명하는 기계적인 문장은 사용하지 마세요.
            """.formatted(userNickname, joinedDiaries);

        return callGeminiApi(prompt);
    }

    // 내부 메서드: 실제 API 호출
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