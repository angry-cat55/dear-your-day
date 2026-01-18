package com.example.dearyourdayserver.controller;

import com.example.dearyourdayserver.dto.user.LoginRequest;
import com.example.dearyourdayserver.dto.user.LoginResponse;
import com.example.dearyourdayserver.dto.user.SignupRequest;
import com.example.dearyourdayserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody SignupRequest request) {
        Long userId = userService.signup(request);

        // 성공하면 "201 Created" 상태코드와 함께 가입된 ID를 줌
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}