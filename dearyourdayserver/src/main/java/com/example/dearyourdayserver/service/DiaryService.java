package com.example.dearyourdayserver.service;

import com.example.dearyourdayserver.dto.diary.DiaryMonthResponse;
import com.example.dearyourdayserver.dto.diary.DiaryResponse;
import com.example.dearyourdayserver.dto.diary.DiaryWriteRequest;
import com.example.dearyourdayserver.entity.Diary;
import com.example.dearyourdayserver.entity.User;
import com.example.dearyourdayserver.repository.DiaryRepository;
import com.example.dearyourdayserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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

        // 4. 저장소에 저장 후 일기 데이터 저장
        Diary savedDiary = diaryRepository.save(diary);

        // 5. 결과 반환 (Entity -> DTO 변환)
        return DiaryResponse.from(savedDiary);
    }

    // 일기 수정 기능
    @Transactional
    public DiaryResponse updateDiary(Long diaryId, DiaryWriteRequest request) {
        // 1. 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 일기를 찾을 수 없습니다."));

        // 2. 작성자 검증
        if (!diary.getUser().getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("수정할 일기의 작성자와 수정 요청을 한 작성자가 일치하지 않습니다.");
        }

        // 3. 일기 내용 수정
        diary.update(request.getContent(), request.getMoodCode());

        // 4. 수정된 일기 DTO 반환
        return DiaryResponse.from(diary);
    }

    // 일기 삭제 기능
    @Transactional
    public void deleteDiary(Long diaryId, Long userId) {
        // 1. 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 일기를 찾을 수 없습니다."));

        // 2. 작성가 검증
        if (!diary.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제할 일기의 작성자와 삭제 요청을 한 작성자가 일치하지 않습니다.");
        }

        // 3. 일기 삭제
        diaryRepository.delete(diary);
    }

    // 특정 날짜의 일기 조회 기능
    @Transactional(readOnly = true)
    public DiaryResponse findByDate(Long userId, LocalDate date) {
        // 1. 유저 객체를 위한 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회에 실패했습니다."));

        // 2. 일기 조회 후 반환 (일기가 없으면 null을 반환 -> 앱에서 null 분기 처리)
        return diaryRepository.findByUserAndWrittenDate(user, date)
                .map(DiaryResponse::from) // 일기 내용 반환
                .orElse(null); // 일기가 없을 경우 null 반환 (앱에서 null일 경우 일기 작성을 하도록 로직 구현)
    }

    // 월별 일기 조회 기능
    @Transactional(readOnly = true)
    public List<DiaryMonthResponse> findAllByMonth(Long userId, int year, int month) {
        // 1. 유저 객체를 위한 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회에 실패했습니다."));

        // 2. 조회 날짜 범위 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 3. 범위 내의 일기 조회
        List<Diary> diaries = diaryRepository.findAllByUserAndWrittenDateBetween(user, startDate, endDate);

        // 4. List<Diary> -> List<DiaryMonthResponse로 변환
        List<DiaryMonthResponse> diaryMonthResponses = new ArrayList<DiaryMonthResponse>();
        for (Diary diary: diaries) {
            diaryMonthResponses.add(DiaryMonthResponse.from(diary));
        }

        // 4. List<DiaryMonthRespones> 형태의 일기 목록 반환
        return diaryMonthResponses;
    }
}