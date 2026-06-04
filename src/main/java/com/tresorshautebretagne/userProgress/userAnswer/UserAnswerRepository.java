package com.tresorshautebretagne.userProgress.userAnswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    Optional<UserAnswer> findFirstByUserIdAndQuestionId(Long userId, Long questionId);
    List<UserAnswer> findByUserIdAndQuestionId(Long userId, Long questionId);
    List<UserAnswer> findByUserId(Long userId);
}
