package com.example.dearyourdayserver.dto.diary;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DiaryWriteRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate writtenDate;

    @NotNull(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "기분 코드는 필수입니다.")
    private String moodCode; // "HAPPY", "SAD" 등
}