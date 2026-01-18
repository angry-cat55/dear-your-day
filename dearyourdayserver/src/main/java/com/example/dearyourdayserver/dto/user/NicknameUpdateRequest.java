package com.example.dearyourdayserver.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameUpdateRequest {
    private String nickname; // 바꿀 닉네임
}
