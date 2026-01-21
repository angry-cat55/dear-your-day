package com.example.dearyourdayserver.service;

import com.example.dearyourdayserver.dto.aisummary.AiSummaryResponse;
import com.example.dearyourdayserver.entity.AiSummary;
import com.example.dearyourdayserver.entity.Diary;
import com.example.dearyourdayserver.entity.User;
import com.example.dearyourdayserver.repository.AiSummaryRepository;
import com.example.dearyourdayserver.repository.DiaryRepository;
import com.example.dearyourdayserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiSummaryService {
    private final AiSummaryRepository aiSummaryRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    // 종합 공감 조회 (생성 or 조회)
    @Transactional
    public AiSummaryResponse getAiSummary(Long userId) {
        // 1. 유저 객체를 위한 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회에 실패했습니다."));

        LocalDate limitDate = LocalDate.now().minusDays(90);
        // 2. 현재 기준 분석 대상 일기 가져오기 (최근 90일 내의 최신순 최대 30개)
        List<Diary> currentDiaries = diaryRepository.findTop30ByUserAndWrittenDateAfterOrderByWrittenDateDesc(user, limitDate);

        if (currentDiaries.isEmpty()) {
            throw new IllegalArgumentException("확인할 일기가 없습니다.");
        }

        // 3. 현재 일기들의 ID를 문자열로 생성 ([100, 90, 82] -> "100, 90, 82")
        String currentDiariesId = currentDiaries.stream()
                .map(diary -> String.valueOf(diary.getDiaryId()))
                .collect(Collectors.joining(","));

        // 4. 저장된 AI 종합 공감 가져오기
        Optional<AiSummary> aiSummary = aiSummaryRepository.findByUser(user);

        // 5. 공감 코멘트 재생성 판단 변수 생성
        boolean needRegenerate = false;

        // 6. AI 종합 공감 코멘트가 없으면 재생성
        if (aiSummary.isEmpty()) {
            needRegenerate = true;
        }
        else {
            AiSummary summary = aiSummary.get();

            // 7. 분석할 일기 구성이 다르면 재생성
            if (!summary.getAnalyzedDiaryIds().equals(currentDiariesId)) {
                needRegenerate = true;

            } else {
                // 8. 현재 조회된 일기들 중 가장 최근 수정된 일기 확인
                LocalDateTime lastUpdated = currentDiaries.stream()
                        .map(Diary::getUpdatedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.MIN);

                // 9. AI 종합 공감 코멘트 생서 이후 수정된 일기가 있을 경우 재생성
                if (lastUpdated.isAfter(summary.getUpdatedAt())) {
                    needRegenerate = true;
                }
            }
        }

        // 10. 결과 처리
        if (needRegenerate) {
            return createNewSummary(user, currentDiaries, currentDiariesId);
        }
        else {
            return AiSummaryResponse.from(aiSummary.get());
        }
    }

    // (내부 메소드) AI API 호출 및 저장
    private AiSummaryResponse createNewSummary(User user, List<Diary> diaries, String diariesId) {

        // 1. 일기 내용을 AI가 이해할 수 있게 날짜 + 내용 포맷팅
        List<String> diaryContents = diaries.stream()
                .map(d -> String.format("[%s] %s", d.getWrittenDate(), d.getContent()))
                .toList();

        // 2. GeminiService 호출해서 종합 공감 코멘트 반환
        String userNickname = user.getNickname();
        String aiSummaryContent = geminiService.getSummary(diaryContents, userNickname);

        // 3. 기존에 저장된 종합 공감 확인
        Optional<AiSummary> existingSummary = aiSummaryRepository.findByUser(user);

        AiSummary aiSummary;

        // 4. 이미 유저의 종합 공감 데이터가 존재할 경우
        if (existingSummary.isPresent()) {
            aiSummary = existingSummary.get();
            aiSummary.updateSummary(aiSummaryContent, diariesId);
        }
        // 5. 없으면 새로 생성
        else {
            aiSummary = AiSummary.builder()
                    .user(user)
                    .summaryContent(aiSummaryContent)
                    .analyzedDiaryIds(diariesId)
                    .build();
        }

        // 6. 생성된 AI 코멘트 DB에 저장 후 반환
        AiSummary savedSummary = aiSummaryRepository.save(aiSummary);

        // 7. 결과 반환
        return AiSummaryResponse.from(savedSummary);
    }
}
