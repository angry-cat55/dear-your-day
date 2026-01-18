package com.example.dearyourdayserver.controller;

import com.example.dearyourdayserver.dto.user.LoginRequest;
import com.example.dearyourdayserver.dto.user.LoginResponse;
import com.example.dearyourdayserver.dto.user.MyInfoResponse;
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
    public ResponseEntity<Long> signup(@RequestBody SignupRequest request) { // 화면 정보를 DTO로 매칭시켜 저장
        Long userId = userService.signup(request); // 서비스 호출해서 회원가입 후 생성된 user_id 반환

        // 성공하면 "201 Created" 상태코드와 함께 가입된 ID 전달
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) { // 화면 정보를 DTO로 매칭시켜 저장
        LoginResponse response = userService.login(request); // 서비스 호출해서 로그인 성공 시 유저 정보 반환

        // 성공하면 "200 OK" 상태코드와 함께 유저 정보 DTO 전달
        return ResponseEntity.ok(response);
    }

    // 내 정보 조회 API
    @GetMapping("/{userId}") // url로 유저 ID 전달
    public ResponseEntity<MyInfoResponse> getInfoById(@PathVariable Long userId) {
        MyInfoResponse response = userService.getInfoById(userId); // 전달받은 유저 ID로 유저 정보 반환
        // 성공하면 "200 OK" 상태코드와 함께 비밀번호를 제외한 모든 유저 정보 DTO 전달
        return ResponseEntity.ok(response);
    }
}