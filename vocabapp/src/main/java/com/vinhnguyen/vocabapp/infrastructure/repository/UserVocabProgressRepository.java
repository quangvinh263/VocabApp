package com.vinhnguyen.vocabapp.infrastructure.repository;

import com.vinhnguyen.vocabapp.domain.entity.UserVocabProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVocabProgressRepository extends JpaRepository<UserVocabProgress, Long> {
}
