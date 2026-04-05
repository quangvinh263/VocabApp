package com.vinhnguyen.vocabapp.api.controller;

import com.vinhnguyen.vocabapp.application.dto.VocabRequest;
import com.vinhnguyen.vocabapp.domain.entity.User;
import com.vinhnguyen.vocabapp.domain.entity.UserVocabProgress;
import com.vinhnguyen.vocabapp.domain.entity.Vocabulary;
import com.vinhnguyen.vocabapp.infrastructure.repository.UserRepository;
import com.vinhnguyen.vocabapp.infrastructure.repository.UserVocabProgressRepository;
import com.vinhnguyen.vocabapp.infrastructure.repository.VocabularyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vocabularies")
public class VocabularyController {

    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;
    private final UserVocabProgressRepository userVocabProgressRepository;

    public VocabularyController(VocabularyRepository vocabularyRepository, UserRepository userRepository, UserVocabProgressRepository userVocabProgressRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.userRepository = userRepository;
        this.userVocabProgressRepository = userVocabProgressRepository;
    }

    // --- HÀM BÍ MẬT: Tự động trích xuất User từ Vòng tay JWT ---
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));
    }

    // 1. THÊM TỪ VỰNG (POST)
    @PostMapping
    public ResponseEntity<?> addVocabulary(@RequestBody VocabRequest request) {
        User currentUser = getCurrentUser(); // Biết ngay ai đang gọi API không cần truyền ID
        if (vocabularyRepository.existsByWordIgnoreCaseAndUser(request.getWord(), currentUser)) {
            return ResponseEntity.badRequest().body("Từ vựng '" + request.getWord() + "' đã tồn tại trong kho của bạn!");
        }
        Vocabulary vocab = Vocabulary.builder()
                .word(request.getWord())
                .meaning(request.getMeaning())
                .partOfSpeech(request.getPartOfSpeech())
                .exampleSentence(request.getExampleSentence())
                .user(currentUser)
                .build();

        Vocabulary savedVocab = vocabularyRepository.save(vocab);

        // TỰ ĐỘNG TẠO TIẾN ĐỘ HỌC (Để xuất hiện trong Nhiệm vụ hôm nay)
        UserVocabProgress progress = UserVocabProgress.builder()
                .user(currentUser)
                .vocabulary(savedVocab)
                .status("NEW")
                .easeFactor(2.5)
                .intervalDays(0)
                .nextReviewDate(LocalDate.now()) // Hẹn luôn hôm nay học
                .build();
        userVocabProgressRepository.save(progress);

        return ResponseEntity.ok("Đã thêm từ vựng và tự động đưa vào danh sách học hôm nay!");
    }

    // 2. XEM DANH SÁCH TỪ VỰNG (GET)
    @GetMapping
    public ResponseEntity<List<Vocabulary>> getMyVocabularies() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Vocabulary> myVocabs = vocabularyRepository.findAllByUser_Username(username);
        return ResponseEntity.ok(myVocabs);
    }

    // 3. SỬA TỪ VỰNG (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVocabulary(@PathVariable Long id, @RequestBody VocabRequest request) {
        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng này!"));

        // Bảo mật: Chống hacker lấy Token của mình đi sửa từ vựng của người khác
        if (!vocab.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            return ResponseEntity.status(403).body("Bạn không có quyền sửa từ vựng của người khác!");
        }

        vocab.setWord(request.getWord());
        vocab.setMeaning(request.getMeaning());
        vocab.setPartOfSpeech(request.getPartOfSpeech());
        vocab.setExampleSentence(request.getExampleSentence());
        vocabularyRepository.save(vocab);

        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 4. XÓA TỪ VỰNG (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVocabulary(@PathVariable Long id) {
        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng này!"));

        if (!vocab.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            return ResponseEntity.status(403).body("Bạn không có quyền xóa từ vựng của người khác!");
        }

        vocabularyRepository.delete(vocab);
        return ResponseEntity.ok("Đã xóa từ vựng!");
    }
}