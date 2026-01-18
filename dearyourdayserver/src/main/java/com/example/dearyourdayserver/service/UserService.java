package com.example.dearyourdayserver.service;

import com.example.dearyourdayserver.dto.user.LoginRequest;
import com.example.dearyourdayserver.dto.user.LoginResponse;
import com.example.dearyourdayserver.dto.user.MyInfoResponse;
import com.example.dearyourdayserver.dto.user.SignupRequest;
import com.example.dearyourdayserver.entity.User;
import com.example.dearyourdayserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 회원가입 기능
    @Transactional
    public Long signup(SignupRequest request) { // 회원가입 시 보낸 정보가 담긴 SignupRequest DTO
        // 1. 아이디 중복 검사
        if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 2. 새로운 유저 만들기
        User user = User.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .build();

        // 3. 저장소에 저장
        User savedUser = userRepository.save(user);

        // 4. 가입된 유저 ID 반환
        return savedUser.getUserId();
    }

    // 로그인 기능
    public LoginResponse login(LoginRequest request) {
        // 1. 아이디로 유저 찾기 (없으면 에러 처리)
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 2. 비밀번호 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // 3. 일치하면 결과 DTO 반환
        return LoginResponse.from(user);
    }

    // 정보 조회 기능
    public MyInfoResponse getInfoById(Long userId) {
        // 1. 유저 ID로 정보 반환
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("정보를 조회할 수 없습니다."));

        // 2. DTO 반환
        return MyInfoResponse.from(user);
    }
}