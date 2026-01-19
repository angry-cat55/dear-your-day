package com.example.dearyourdayserver.repository;

import com.example.dearyourdayserver.entity.AiSummary;
import com.example.dearyourdayserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiSummaryRepository extends JpaRepository<AiSummary, Long> {
    // 유저의 종합 요약본 조회
    Optional<AiSummary> findByUser(User user);
}
