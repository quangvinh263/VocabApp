package com.vinhnguyen.vocabapp.api.controller;

import com.vinhnguyen.vocabapp.application.dto.ReviewRequest;
import com.vinhnguyen.vocabapp.application.service.SpacedRepetitionService;
import com.vinhnguyen.vocabapp.domain.entity.UserVocabProgress;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Đánh dấu đây là API Controller
@RequestMapping("/api/study") // Đường dẫn gốc cho toàn bộ API trong class này
public class StudyController {
    private final SpacedRepetitionService spacedRepetitionService;

    public StudyController(SpacedRepetitionService spacedRepetitionService) {
        this.spacedRepetitionService = spacedRepetitionService;
    }
    /**
     * API: Nhận kết quả học tập từ Client và trả về tiến độ mới
     * Method: POST
     * URL: http://localhost:8080/api/study/review
     */
    @PostMapping("/review")
    public ResponseEntity<UserVocabProgress> submitReview(@RequestBody ReviewRequest request) {
        // Gọi "bộ não" (Service) để xử lý thuật toán
        UserVocabProgress updatedProgress = spacedRepetitionService.reviewVocabulary(
                request.getProgressId(),
                request.getQuality()
        );
        return ResponseEntity.ok(updatedProgress);
    }
}
