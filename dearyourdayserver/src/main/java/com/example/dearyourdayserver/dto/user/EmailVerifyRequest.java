package com.example.dearyourdayserver.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerifyRequest {
    private String email;
    private String authCode; // 사용자가 입력한 번호
}