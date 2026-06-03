package com.tresorshautebretagne.userProgress;

import com.tresorshautebretagne.userProgress.userAnswer.UserAnswer;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswerRepository;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.question.QuestionRepository;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepRepository;
import com.tresorshautebretagne.treasureHunt.TreasureHunt;
import com.tresorshautebretagne.treasureHunt.TreasureHuntRepository;
import com.tresorshautebretagne.user.User;
import com.tresorshautebretagne.user.UserRepository;
import com.tresorshautebretagne.shared.service.MapperService;
import com.tresorshautebretagne.shared.service.CoordinateCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final StepRepository stepRepository;
    private final TreasureHuntRepository treasureHuntRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;
    private final CoordinateCalculationService coordinateService;

    @Transactional
    public UserProgressDTO startTreasureHunt(Long userId, Long treasureHuntId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        TreasureHunt hunt = treasureHuntRepository.findById(treasureHuntId)
                .orElseThrow(() -> new RuntimeException("Treasure hunt not found: " + treasureHuntId));

        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(userId, treasureHuntId)
                .orElse(new UserProgress());

        progress.setUser(user);
        progress.setTreasureHunt(hunt);
        progress.setCurrentStep(1);
        progress.setIsCompleted(false);
        progress.setIsTreasureUnlocked(false);

        UserProgress saved = userProgressRepository.save(progress);
        return mapperService.userProgressToDTO(saved);
    }

    @Transactional
    public void submitAnswer(Long userId, Long questionId, String answer) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found: " + questionId));

        String normalizedAnswer = answer.trim().toLowerCase();
        String normalizedCorrect = question.getCorrectAnswer().trim().toLowerCase();
        
        Boolean isCorrect = normalizedAnswer.equals(normalizedCorrect);

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUser(user);
        userAnswer.setQuestion(question);
        userAnswer.setAnswer(answer);
        userAnswer.setIsCorrect(isCorrect);

        userAnswerRepository.save(userAnswer);
    }

    public UserProgressDTO getUserProgress(Long userId, Long treasureHuntId) {
        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(userId, treasureHuntId)
                .orElseThrow(() -> new RuntimeException("No progress found"));

        return mapperService.userProgressToDTO(progress);
    }

    public List<UserProgressDTO> getUserProgresses(Long userId) {
        return userProgressRepository.findByUserId(userId)
                .stream()
                .map(mapperService::userProgressToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAndUnlockTreasure(Long userId, Long treasureHuntId) {
        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(userId, treasureHuntId)
                .orElseThrow(() -> new RuntimeException("No progress found"));

        TreasureHunt hunt = progress.getTreasureHunt();
        List<Step> steps = stepRepository.findByTreasureHuntIdOrderByStepOrder(hunt.getId());

        boolean allCorrect = steps.stream().allMatch(step -> 
            allQuestionsAnsweredCorrectly(userId, step.getId())
        );

        if (allCorrect) {
            progress.setIsCompleted(true);
            progress.setIsTreasureUnlocked(true);
            progress.setCompletedAt(LocalDateTime.now());
            userProgressRepository.save(progress);
        }
    }

    public boolean allQuestionsAnsweredCorrectly(Long userId, Long stepId) {
        List<Question> questions = questionRepository.findByStepIdOrderByQuestionOrder(stepId);
        
        if (questions.isEmpty()) {
            return true;
        }

        return questions.stream().allMatch(question -> {
            List<UserAnswer> answers = userAnswerRepository.findByUserIdAndQuestionId(userId, question.getId());
            return !answers.isEmpty() && answers.stream().allMatch(UserAnswer::getIsCorrect);
        });
    }

    @Transactional
    public CoordinateCalculationService.CalculatedCoordinates calculateTreasureCoordinates(Long userId, Long treasureHuntId) {
        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(userId, treasureHuntId)
                .orElseThrow(() -> new RuntimeException("No progress found"));

        if (!progress.getIsTreasureUnlocked()) {
            throw new RuntimeException("Treasure not yet unlocked");
        }

        TreasureHunt hunt = progress.getTreasureHunt();
        return coordinateService.calculateCoordinates(userId, hunt);
    }

    @Transactional
    public void advanceStep(Long userId, Long treasureHuntId) {
        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(userId, treasureHuntId)
                .orElseThrow(() -> new RuntimeException("No progress found"));

        TreasureHunt hunt = progress.getTreasureHunt();
        int totalSteps = stepRepository.findByTreasureHuntIdOrderByStepOrder(hunt.getId()).size();

        if (progress.getCurrentStep() < totalSteps) {
            progress.setCurrentStep(progress.getCurrentStep() + 1);
            userProgressRepository.save(progress);
        }
    }
}
