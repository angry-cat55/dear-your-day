package com.example.dearyourdayserver.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ai_summaries")
public class AiSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summaryContent;

    @Column(nullable = false)
    private LocalDate analyzedUntilDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public AiSummary(User user, String summaryContent, LocalDate analyzedUntilDate) {
        this.user = user;
        this.summaryContent = summaryContent;
        this.analyzedUntilDate = analyzedUntilDate;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간 자동 저장
    }
}