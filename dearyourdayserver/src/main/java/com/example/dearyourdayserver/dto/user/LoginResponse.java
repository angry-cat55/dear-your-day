package com.example.dearyourdayserver.dto.user;

import com.example.dearyourdayserver.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class LoginResponse {
    private Long userId; // 로그인한 user_id
    private String nickname; // 로그인한 유저의 nickname
    private String loginId; // 로그인 ID
    private String email; // 이메일
    private LocalDateTime createdAt; // 계정 생성 일자

    // Entity -> DTO 변환
    public static LoginResponse from(User user) {
        return LoginResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
