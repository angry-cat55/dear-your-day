package com.example.dearyourdayserver.service;

import com.example.dearyourdayserver.entity.Diary;
import com.example.dearyourdayserver.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCommentService {

    private final GeminiService geminiService;
    private final DiaryRepository diaryRepository;

    @Async // 별도 스레드에서 실행 (비동기 처리)
    @Transactional
    public void generateAndSaveComment(Long diaryId) {
        log.info("비동기 AI 코멘트 생성 시작 - Diary ID: {}", diaryId);

        try {
            // 1. 일기 조회
            Diary diary = diaryRepository.findById(diaryId)
                    .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

            // 2. Gemini API 호출
            String comment = geminiService.getCoachingComment(diary.getContent());

            // 3. 결과 저장
            diary.updateAiComment(comment);

            log.info("AI 코멘트 저장 완료: {}", comment);

        } catch (Exception e) {
            log.error("AI 코멘트 생성 실패: {}", e.getMessage());
        }
    }
}