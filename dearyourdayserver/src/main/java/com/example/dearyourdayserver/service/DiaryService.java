package com.example.dearyourdayserver.service;

import com.example.dearyourdayserver.dto.diary.DiaryResponse;
import com.example.dearyourdayserver.dto.diary.DiaryWriteRequest;
import com.example.dearyourdayserver.entity.Diary;
import com.example.dearyourdayserver.entity.User;
import com.example.dearyourdayserver.repository.DiaryRepository;
import com.example.dearyourdayserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    // 일기 작성 기능
    @Transactional
    public DiaryResponse writeDiary(DiaryWriteRequest request) {
        // 1. 누가 쓰는 건지 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 해당 날짜에 이미 쓴 일기가 있는지 확인
        if (diaryRepository.findByUserAndWrittenDate(user, request.getWrittenDate()).isPresent()) {
            throw new IllegalArgumentException("해당 날짜에는 이미 일기가 있습니다.");
        }

        // 3. 일기 엔티티 생성
        Diary diary = Diary.builder()
                .user(user)
                .content(request.getContent())
                .writtenDate(request.getWrittenDate())
                .moodCode(request.getMoodCode())
                .build();

        // 4. 저장소에 저장
        Diary savedDiary = diaryRepository.save(diary);

        // 5. 결과 반환 (Entity -> DTO 변환)
        return DiaryResponse.from(savedDiary);
    }
}