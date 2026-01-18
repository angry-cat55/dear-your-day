package com.example.dearyourdayserver.dto.user;

import com.example.dearyourdayserver.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long userId; // 로그인한 user_id
    private String nickname; // 로그인한 유저의 nickname

    // Entity -> DTO 변환
    public static LoginResponse from(User user) {
        return LoginResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }
}
