package com.example.dearyourdayserver.controller;

import com.example.dearyourdayserver.dto.user.*;
import com.example.dearyourdayserver.service.EmailService;
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
    private final EmailService emailService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody SignupRequest request) { // 화면 정보를 DTO로 매칭시켜 저장
        userService.signup(request); // 서비스 호출해서 DB에 데이터 생성

        // 성공하면 "204 No Content" 상태코드 전달
        return ResponseEntity.noContent().build();
    }

    // 아이디 중복 확인 API
    @GetMapping("/checkId")
    public ResponseEntity<Boolean> checkId(@RequestParam String loginId) {
        Boolean isDuplicate = userService.isLoginIdDuplicate(loginId); // 서비스 호출해서 아이디 중복 여부 반환.
        // 중복일 경우 true, 없을 경우 false 반환

        // 반환 성공하면 "200 Ok" 상태코드와 함께 결과 전달
        return ResponseEntity.ok(isDuplicate);
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

    // 닉네임 수정 API
    @PatchMapping("/{userId}/nickname") // url로 유저 ID 전달
    public ResponseEntity<String> updateNickname(
            @PathVariable Long userId, // 유저 ID
            @RequestBody NicknameUpdateRequest request) { // 화면에서 전달된 새 닉네임
        String newNickname = request.getNickname();
        userService.updateNickname(userId, newNickname); // 서비스에서 새 닉네임으로 변경

        // 성공하면 "200 OK" 상태코드와 함께 새 닉네임 String 전달
        return ResponseEntity.ok(newNickname);
    }

    // 이메일 인증번호 요청 API
    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        // 화면에서 전달된 이메일로 인증번호 발송
        emailService.sendEmail(request.getEmail());

        // 성공하면 "200 OK" 상태코드와 함께 문자열 전달
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    // 이메일 인증번호 확인 요청
    @PostMapping("/email/verify")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody EmailVerifyRequest request) {
        // 전달받은 이메일과 인증번호로 일치여부 확인 후 저장
        boolean isVerified = emailService.verifyEmailCode(request.getEmail(), request.getAuthCode());

        // 확인 성공하면 "200 OK" 상태코드와 함께 일치여부 반환
        return ResponseEntity.ok(isVerified);
    }

}