package com.vinhnguyen.vocabapp.application.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long progressId; //ID của từ vựng đang học
    private int quality;     // Điểm đánh giá (1, 2, 3, 4)
}
