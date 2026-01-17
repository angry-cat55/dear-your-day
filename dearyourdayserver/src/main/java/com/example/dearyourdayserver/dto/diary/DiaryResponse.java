package com.example.dearyourdayserver.dto.diary;

import com.example.dearyourdayserver.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DiaryResponse {

    private Long diaryId;
    private String content;
    private LocalDate writtenDate;
    private String moodCode;
    private String aiComment;

    // Entity -> DTO 변환
    public static DiaryResponse from(Diary diary) {
        return DiaryResponse.builder()
                .diaryId(diary.getDiaryId())
                .content(diary.getContent())
                .writtenDate(diary.getWrittenDate())
                .moodCode(diary.getMoodCode())
                .aiComment(diary.getAiComment())
                .build();
    }
}