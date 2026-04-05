package com.vinhnguyen.vocabapp.infrastructure.repository;

import com.vinhnguyen.vocabapp.domain.entity.User;
import com.vinhnguyen.vocabapp.domain.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    // Spring Data JPA sẽ tự dịch câu này thành SQL tìm từ vựng theo tên User
    List<Vocabulary> findAllByUser_Username(String username);

    //Kiểm tra từ vựng đã tồn tại chưa (bỏ qua viết hoa/thường)
    boolean existsByWordIgnoreCaseAndUser(String word, User user);
}