package com.tresorshautebretagne.repository;

import com.tresorshautebretagne.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByStepIdOrderByQuestionOrder(Long stepId);
}
