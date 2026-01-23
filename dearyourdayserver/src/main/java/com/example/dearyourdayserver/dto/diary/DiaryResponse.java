package com.example.dearyourdayserver.dto.diary;

import com.example.dearyourdayserver.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class DiaryResponse {

    private Long diaryId; // 조회한 일기 id
    private String content; // 조회한 일기 내용
    private LocalDate writtenDate; // 조회한 일기의 일자
    private String moodCode; // 조회한 일기에 저장된 기분 코드
    private LocalDateTime updatedAt; // 조회한 일기를 작성(마지막으로 수정)한 시간
    private String aiComment; // 조회한 일기에 해당되는 ai 공감 코멘트

    // Entity -> DTO 변환
    public static DiaryResponse from(Diary diary) {
        return DiaryResponse.builder()
                .diaryId(diary.getDiaryId())
                .content(diary.getContent())
                .writtenDate(diary.getWrittenDate())
                .moodCode(diary.getMoodCode())
                .updatedAt(diary.getUpdatedAt())
                .aiComment(diary.getAiComment())
                .build();
    }
}