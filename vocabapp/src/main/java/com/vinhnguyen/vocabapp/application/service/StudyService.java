package com.vinhnguyen.vocabapp.application.service;

import com.vinhnguyen.vocabapp.application.dto.StudyStatsResponse;
import com.vinhnguyen.vocabapp.domain.entity.UserVocabProgress;
import com.vinhnguyen.vocabapp.infrastructure.repository.UserVocabProgressRepository;
import com.vinhnguyen.vocabapp.infrastructure.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final UserVocabProgressRepository userVocabProgressRepository;
    private final VocabularyRepository vocabularyRepository;

    /**
     * Lấy thống kê học tập dựa trên Status trong Entity UserVocabProgress
     */
    public StudyStatsResponse getStats(Long userId) {
        // 1. Tổng số từ vựng hiện có trong hệ thống
        long totalVocabs = vocabularyRepository.count();

        // 2. Số từ đã đạt trạng thái MASTERED
        long mastered = userVocabProgressRepository.countByUserIdAndStatus(userId, "MASTERED");

        // 3. Tổng số từ đã có tiến độ (không phân biệt status)
        long totalLearned = userVocabProgressRepository.countByUserId(userId);

        // 4. Số từ đang học (Đã bắt đầu nhưng chưa MASTERED)
        long learning = totalLearned - mastered;

        // 5. Số từ mới (Chưa bao giờ xuất hiện trong bảng progress)
        long newWords = totalVocabs - totalLearned;

        return new StudyStatsResponse(totalVocabs, learning, mastered, newWords);
    }

    // Sau này các hàm xử lý thuật toán SM-2 (update intervalDays, nextReviewDate) sẽ nằm ở đây
}