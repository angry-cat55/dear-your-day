package com.example.dearyourdayserver.repository;

import com.example.dearyourdayserver.dto.diary.DiaryMonthResponse;
import com.example.dearyourdayserver.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    
    // 특정 유저가 특정 날짜에 쓴 일기 찾는 메소드
    Optional<Diary> findByUserAndWrittenDate(User user, LocalDate writtenDate);
    // 특정 유저의 특정 기간 일기 모두 가져오는 메소드
    List<Diary> findAllByUserAndWrittenDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
