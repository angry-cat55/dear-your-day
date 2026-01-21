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
    private static final String GEMINI_MODEL_PATH = "/v1beta/models/gemma-3-12b-it:generateContent";

    // 일기 하나에 대한 공감 코멘트
    public String getCoachingComment(String diaryContent, String userNickname) {
        String prompt =
            """
            당신은 사용자의 일기를 읽고 마음 깊이 공감해주는 '가장 친한 친구'입니다.
            상담가처럼 분석하려 들거나 사용자의 하루에 대해 평가하거나 가르치려 하지 말고, 오직 그 '감정'에만 집중해서 사용자가 느끼는 감정의 무게를 묵묵히 함께 짊어지며 대화하듯 반응해주세요.
            
            [작성 요구사항]
            1. 길이는 100자 내외로 짧고 간결하게 작성하세요.
            2. 말투는 다정하고 부드러운 한국어 '해요체'를 쓰세요.
            
            [제약 사항(매우 중요)]
            - "오늘 ~하셨군요" 처럼 일기 내용을 기계적으로 다시 읊지 마세요.
            - 일기 내용을 요약하지 말고, 사용자가 느꼈을 '감정'에 집중해서 공감해 주세요.
            - 해결책을 제시하기보다 "저라면 정말 속상했을 것 같아요", "그 순간 얼마나 행복했을지 상상이 가요" 처럼 감정에 동조해주세요.
            - 기계적이고 상투적인 "힘내세요", "응원합니다"는 피하고, 진심 어린 공감을 건네주세요.
            - "이별하셔서 힘드시겠어요"처럼 원인과 결과를 굳이 연결 지어 설명하지 마세요. 이유를 설명하는 대신, "지금은 숨 쉬는 것조차 버겁게 느껴지실 것 같아요"처럼 현재의 상태에만 집중하세요.
            - 지나치게 비극적인 표현("가슴이 찢어지네요")보다는 차분하고 진중한 위로를 건네세요.
            
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
            당신은 사용자의 지난 기록을 통해 내면의 성장을 발견해주는 '심리 에세이 작가'입니다.
            아래 제공되는 90일간의 일기들을 읽고, '종합 공감 리포트'를 작성해주세요.
            
            [사용자 닉네임]
            %s
            
            [분석 일기 내용 목록]
            %s
            
            [작성 요구사항]
            위 일기들을 분석하여 다음 3가지 항목으로 구성된 글을 작성해 주세요.
            1. 지난 시간의 서사: 사용자의 감정이 시간의 흐름에 따라 어떻게 변화했는지 담백하게 서술해 주세요. (예: "초반에는 새로운 환경 때문에 긴장감이 가득했지만, 점차 자신만의 요령을 터득하며 여유를 찾아가는 과정이 보입니다.")
            2. 마음의 중심: 단순히 많이 쓴 단어를 찾는 게 아니라, 사용자가 진정으로 중요하게 생각하는 가치나 현재 몰입하고 있는 것이 무엇인지 깊이 있게 짚어주세요. (예: "성장에 대한 열망이 느껴집니다")
            3. 따뜻한 응원: 분석한 내용을 토대로, 친한 친구가 어깨를 토닥여주듯 앞으로의 날들에 대한 기대와 따뜻한 격려의 말을 전해 주세요.
            
            [제약 사항(매우 중요)]
            - 절대로 "안녕하세요", "일기를 읽어보았습니다", "통찰과 위로의 편지" 같은 제목이나 인사말로 시작하지 마세요.
            - 마크다운(**, ## 등)을 절대 사용하지 마세요. 오직 순수 텍스트(Plain Text)로만 작성하세요.
            - 절대로 "1월 20일에는...", "21일에는..." 처럼 특정 날짜나 사건을 나열하지 마세요.
            - "'코딩', '테스트' 같은 키워드가 등장했습니다" 식의 기계적인 분석 문장을 쓰지 마세요.
            - 말투는 부드러운 존댓말(해요체)로, 전체 분량은 공백 포함 400~500자 내외로 작성해 주세요.
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