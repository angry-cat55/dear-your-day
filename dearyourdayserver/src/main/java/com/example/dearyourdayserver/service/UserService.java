package com.example.dearyourdayserver.service;

import com.example.dearyourdayserver.dto.user.SignupRequest;
import com.example.dearyourdayserver.entity.User;
import com.example.dearyourdayserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Repository를 자동으로 연결해줌 (의존성 주입)
@Transactional(readOnly = true) // 기본적으로는 읽기만 함 (안전)
public class UserService {

    private final UserRepository userRepository;

    // 회원가입 기능
    @Transactional
    public Long signup(SignupRequest request) {
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
}