package com.vinhnguyen.vocabapp.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_vocab_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVocabProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thiết lập Khóa ngoại (Foreign Key) trỏ tới bảng users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Thiết lập Khóa ngoại trỏ tới bảng vocabularies
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(nullable = false)
    private String status; // Trạng thái: NEW, LEARNING, REVIEWING, MASTERED

    @Column(nullable = false)
    private double easeFactor = 2.5; // Hệ số dễ mặc định của thuật toán SM-2

    @Column(nullable = false)
    private int intervalDays = 0; // Khoảng cách ngày ôn tập

    @Column(nullable = false)
    private LocalDate nextReviewDate; // Ngày phải ôn tập tiếp theo
}