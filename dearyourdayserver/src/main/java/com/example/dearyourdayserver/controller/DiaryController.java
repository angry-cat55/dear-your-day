package com.example.dearyourdayserver.controller;

import com.example.dearyourdayserver.dto.diary.DiaryMonthResponse;
import com.example.dearyourdayserver.dto.diary.DiaryResponse;
import com.example.dearyourdayserver.dto.diary.DiaryWriteRequest;
import com.example.dearyourdayserver.service.AiCommentService;
import com.example.dearyourdayserver.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final AiCommentService aiCommentService;

    // 일기 작성 API
    @PostMapping
    public ResponseEntity<DiaryResponse> writeDiary(@RequestBody DiaryWriteRequest request) { // 일기 데이터를 DTO로 매칭시켜 저장
        DiaryResponse response = diaryService.writeDiary(request); // 서비스 호출해서 일기 저장 후 일기 정보 반환 

        // 일기 저장 후, 비공기 AI 코멘트 생성 요청
        aiCommentService.generateAndSaveComment(response.getDiaryId());

        // 성공하면 "201 Created" 상태코드와 함께 일기 정보 DTO 전달
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 일기 수정 API
    @PutMapping("/{diaryId}") // url로 일기 ID 전달
    public ResponseEntity<DiaryResponse> updateDiary(
            @PathVariable Long diaryId, // 일기 ID
            @RequestBody DiaryWriteRequest request) // 화면에서 전달된 새 일기 데이터
    {
        // 일기 업데이트 후 새 일기 데이터 반환
        DiaryResponse response = diaryService.updateDiary(diaryId, request);

        // 성공하면 "201 Created" 상태코드와 함께 새 일기 정보 DTO 전달
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 일기 삭제 API
    @DeleteMapping("/{diaryId}") // url로 일기 ID 전달
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long diaryId, // 일기 ID
            @RequestParam Long userId) // 쿼리 파라미터("?userId=1")로 전달받은 유저 ID
    {
        // 일기 삭제
        diaryService.deleteDiary(diaryId, userId);

        // 성공하면 "204 No Content" 상태코드 전달
        return ResponseEntity.noContent().build();
    }

    // 특정 날짜(앱 실행 시의 날짜(오늘) 포함) 일기 조회
    @GetMapping
    public ResponseEntity<DiaryResponse> findDiaryByDate(
            @RequestParam Long userId, LocalDate date // 쿼리 파라미터로 전달받은 유저 ID와 조회할 일기 날짜
    ) {
        // userId와 date를 통해 한 일기 데이터 조회 후 저장
        DiaryResponse response = diaryService.findByDate(userId, date);

        // 성공하면 "200 OK" 상태코드와 함께 일기 정보 DTO 전달
        return ResponseEntity.ok(response);
    }

    // 월별 일기 목록 조회
    @GetMapping("/monthly")
    public ResponseEntity<List<DiaryMonthResponse>> findDiariesByMonth(
            @RequestParam Long userId, int year, int month // 쿼리 파라미터로 전달 받은 유저 ID와 조회할 달
    ) {
        // 유저의 월별 일기 데이터(diaryId, writtenDate, moodCode) 조회 후 저장
        List<DiaryMonthResponse> monthlyDiaryList = diaryService.findAllByMonth(userId, year, month);

        // 성공하면 "200 OK" 상태코드와 함께 일기 리스트 DTO 전달
        return ResponseEntity.ok(monthlyDiaryList);
    }
}