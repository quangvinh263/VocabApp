package com.vinhnguyen.vocabapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyStatsResponse {
    private long total;     // Tổng số từ trong kho
    private long learned;   // Số từ đang trong quá trình học (có trong VocabProgress)
    private long mastered;  // Số từ đã thuộc
    private long newWords;  // Số từ mới tinh (chưa bao giờ học)
}
