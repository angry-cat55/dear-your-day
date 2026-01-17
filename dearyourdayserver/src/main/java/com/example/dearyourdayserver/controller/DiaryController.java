package com.example.dearyourdayserver.controller;

import com.example.dearyourdayserver.dto.diary.DiaryResponse;
import com.example.dearyourdayserver.dto.diary.DiaryWriteRequest;
import com.example.dearyourdayserver.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    // 일기 작성 API
    @PostMapping
    public ResponseEntity<DiaryResponse> writeDiary(@RequestBody DiaryWriteRequest request) {
        DiaryResponse response = diaryService.writeDiary(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}