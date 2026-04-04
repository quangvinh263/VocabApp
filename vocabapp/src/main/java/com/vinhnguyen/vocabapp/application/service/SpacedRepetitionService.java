package com.vinhnguyen.vocabapp.application.service;

import com.vinhnguyen.vocabapp.api.exception.ResourceNotFoundException;
import com.vinhnguyen.vocabapp.domain.entity.UserVocabProgress;
import com.vinhnguyen.vocabapp.infrastructure.repository.UserVocabProgressRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SpacedRepetitionService {
    private final UserVocabProgressRepository progressRepository;

    public SpacedRepetitionService(UserVocabProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    /**
     * Hàm xử lý logic thuật toán SM-2
     * @param progressId ID của tiến độ học
     * @param quality Độ khó người dùng đánh giá (1: Again, 2: Hard, 3: Good, 4: Easy)
     */
    public UserVocabProgress reviewVocabulary(Long progressId, int quality) {
        // 1. Tìm bản ghi tiến độ trong Database
        UserVocabProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("Tiến độ học với ID " + progressId + " không tồn tại."));

        int currentInterval = progress.getIntervalDays();
        double currentEase = progress.getEaseFactor();

        // 2. Logic cập nhật theo thuật toán SM-2 rút gọn
        if (quality < 3) {
            // Nếu bấm Again(1) hoặc Hard(2): Bị phạt, phải học lại sớm
            currentInterval = 1;
            // Giảm hệ số dễ (Ease Factor), không bao giờ để thấp hơn 1.3
            currentEase = Math.max(1.3, currentEase - 0.2);
            progress.setStatus("LEARNING");
        } else {
            // Nếu bấm Good(3) hoặc Easy(4): Thuộc bài, giãn thời gian ôn tập
            if (currentInterval == 0) {
                currentInterval = 1; // Học lần đầu -> Mai ôn lại
            } else if (currentInterval == 1) {
                currentInterval = 3; // Mới ôn lần 1 -> 3 ngày sau ôn lại
            } else {
                // Các lần sau: Khoảng cách = Khoảng cách cũ * Hệ số dễ
                currentInterval = (int) Math.round(currentInterval * currentEase);
            }

            // Tăng hệ số dễ nếu bấm Easy(4)
            if (quality == 4) {
                currentEase += 0.15;
            }
            progress.setStatus("REVIEWING");
        }

        // 3. Cập nhật dữ liệu mới vào Entity
        progress.setIntervalDays(currentInterval);
        progress.setEaseFactor(currentEase);
        // Ngày học tiếp theo = Hôm nay + số ngày Interval
        progress.setNextReviewDate(LocalDate.now().plusDays(currentInterval));

        // 4. Lưu ngược lại xuống Database
        return progressRepository.save(progress);
    }
}
