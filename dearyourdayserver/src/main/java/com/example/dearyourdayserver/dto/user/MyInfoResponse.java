package com.example.dearyourdayserver.dto.user;

import com.example.dearyourdayserver.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class MyInfoResponse {
    private Long userId; // 내부 식별용 유저 ID
    private String loginId; // 로그인 ID
    private String nickname; // nickname
    private String phoneNumber; // 휴대폰 번호
    private String createdAt; // 계정 생성 일자

    private static String dateFormat(LocalDateTime date) {
        if (date == null)
            return null;
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Entity -> DTO 변환
    public static MyInfoResponse from(User user) {
        return MyInfoResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(MyInfoResponse.dateFormat(user.getCreatedAt()))
                .build();
    }
}