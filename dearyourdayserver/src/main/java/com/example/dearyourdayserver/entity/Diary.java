package com.example.dearyourdayserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diaries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "written_date"})
        })
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDate writtenDate;

    @Column(nullable = false, length = 20)
    private String moodCode;

    @Column(columnDefinition = "TEXT")
    private String aiComment;

    private LocalDateTime aiGeneratedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Diary(User user, String content, LocalDate writtenDate, String moodCode) {
        this.user = user;
        this.content = content;
        this.writtenDate = writtenDate;
        this.moodCode = moodCode;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 내용 수정할 때 쓸 메소드
    public void update(String content, String moodCode) {
        this.content = content;
        this.moodCode = moodCode;
        this.updatedAt = LocalDateTime.now();
        this.aiComment = null;
        this.aiGeneratedAt = null;
    }

    // 기분 코드만 변경할 때 쓸 메소드
    public void updateMood(String moodCode) {
        this.moodCode = moodCode;
        this.updatedAt = LocalDateTime.now();
    }

    // AI 코멘트 달릴 때 쓸 메소드
    public void updateAiComment(String aiComment) {
        this.aiComment = aiComment;
        this.aiGeneratedAt = LocalDateTime.now();
    }
}