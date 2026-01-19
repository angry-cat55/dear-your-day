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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String analyzedDiaryIds;

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Builder
    public AiSummary(User user, String summaryContent, String analyzedDiaryIds) {
        this.user = user;
        this.summaryContent = summaryContent;
        this.analyzedDiaryIds = analyzedDiaryIds;
        this.updatedAt = LocalDateTime.now(); // 객체 생성 시 현재 시간 자동 저장
    }

    // 내용 업데이트용 메소드
    public void updateSummary(String newContent, String newIds) {
        this.summaryContent = newContent;
        this.analyzedDiaryIds = newIds;
        this.updatedAt = LocalDateTime.now();
    }
}