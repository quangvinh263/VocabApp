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
        // 1. Chỉ đếm những từ thuộc về user này
        long totalVocabsOfUser = vocabularyRepository.countByUserId(userId);

        // 2. Số từ đã MASTERED
        long mastered = userVocabProgressRepository.countByUserIdAndStatus(userId, "MASTERED");

        // 3. Tổng số từ có tiến độ
        long totalLearned = userVocabProgressRepository.countByUserId(userId);

        // 4. Số từ mới
        long newWords = userVocabProgressRepository.countByUserIdAndStatus(userId, "NEW");

        // 5. Số từ đang học
        long learning = totalLearned - mastered - newWords;

        return new StudyStatsResponse(totalVocabsOfUser, learning, mastered, newWords);
    }

    // Sau này các hàm xử lý thuật toán SM-2 (update intervalDays, nextReviewDate) sẽ nằm ở đây
}