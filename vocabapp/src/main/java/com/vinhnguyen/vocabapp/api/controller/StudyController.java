package com.vinhnguyen.vocabapp.api.controller;

import com.vinhnguyen.vocabapp.application.dto.ReviewRequest;
import com.vinhnguyen.vocabapp.application.dto.StudyStatsResponse;
import com.vinhnguyen.vocabapp.application.service.SpacedRepetitionService;
import com.vinhnguyen.vocabapp.application.service.StudyService;
import com.vinhnguyen.vocabapp.domain.entity.User;
import com.vinhnguyen.vocabapp.domain.entity.UserVocabProgress;
import com.vinhnguyen.vocabapp.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Đánh dấu đây là API Controller
@RequestMapping("/api/study")// Đường dẫn gốc cho toàn bộ API trong class này
public class StudyController {
    private final SpacedRepetitionService spacedRepetitionService;
    private final UserRepository userRepository;
    private final StudyService studyService;

    public StudyController(SpacedRepetitionService spacedRepetitionService, UserRepository userRepository, StudyService studyService) {
        this.spacedRepetitionService = spacedRepetitionService;
        this.userRepository = userRepository;
        this.studyService = studyService;
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

    // API lấy danh sách từ vựng cần ôn tập hôm nay
    @GetMapping("/today")
    public ResponseEntity<List<UserVocabProgress>> getTodayTasks() {
        // Lấy thông tin user hiện tại từ JWT
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserVocabProgress> tasks = spacedRepetitionService.getDailyTasks(user.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/stats")
    public ResponseEntity<StudyStatsResponse> getStats(Authentication authentication) {
        // Giả sử bạn đã có logic lấy UserID từ Authentication (JWT)
        // Nếu chưa, hãy tạm để một ID cứng (ví dụ: 1L) để test giao diện trước
        Long userId = 1L;

        return ResponseEntity.ok(studyService.getStats(userId));
    }
}
