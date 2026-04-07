package com.vinhnguyen.vocabapp.infrastructure.repository;

import com.vinhnguyen.vocabapp.domain.entity.UserVocabProgress;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserVocabProgressRepository extends JpaRepository<UserVocabProgress, Long> {
    // Tìm các từ vựng cần ôn tập: ngày hẹn <= ngày hiện tại
    @Query("SELECT p FROM UserVocabProgress p WHERE p.user.id = :userId AND p.nextReviewDate <= :today")
    List<UserVocabProgress> findTodaysTasks(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Modifying
    @Transactional
    void deleteByVocabularyId(Long vocabularyID);

    // Đếm tổng số từ user này đã tương tác (LEARNING + REVIEWING + MASTERED)
    long countByUserId(Long userId);

    // Đếm số từ đã thuộc hẳn theo Status
    long countByUserIdAndStatus(Long userId, String status);
}
