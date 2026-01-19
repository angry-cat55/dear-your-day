package com.example.dearyourdayserver.dto.diary;

import com.example.dearyourdayserver.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import com.example.dearyourdayserver.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DiaryMonthResponse {

    private Long diaryId; // 조회한 일기 id
    private LocalDate writtenDate; // 조회한 일기의 일자
    private String moodCode; // 조회한 일기에 저장된 기분 코드

    // Entity -> DTO 변환
    public static DiaryMonthResponse from(Diary diary) {
        return DiaryMonthResponse.builder()
                .diaryId(diary.getDiaryId())
                .writtenDate(diary.getWrittenDate())
                .moodCode(diary.getMoodCode())
                .build();
    }
}