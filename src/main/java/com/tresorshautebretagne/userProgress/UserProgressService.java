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
import java.util.Comparator;
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
    public UserProgressDTO startTreasureHunt(String userEmail, Long huntId) {
        User user = findUserByEmail(userEmail);
        TreasureHunt hunt = treasureHuntRepository.findById(huntId)
                .orElseThrow(() -> new RuntimeException("Treasure hunt not found: " + huntId));

        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(user.getId(), huntId)
                .orElse(new UserProgress());

        // Sauvegarder firstCompletedAt si déjà défini (ne jamais l'effacer)
        LocalDateTime existingFirstCompleted = progress.getFirstCompletedAt();

        progress.setUser(user);
        progress.setTreasureHunt(hunt);
        progress.setCurrentStep(1);
        progress.setIsCompleted(false);
        progress.setIsTreasureUnlocked(false);
        progress.setCompletedAt(null);

        // Restaurer firstCompletedAt
        if (existingFirstCompleted != null) {
            progress.setFirstCompletedAt(existingFirstCompleted);
        }

        return mapperService.userProgressToDTO(userProgressRepository.save(progress));
    }

    @Transactional
    public SubmitAnswersResultDTO submitAnswers(String userEmail, Long huntId, Long stepId,
                                                List<SubmitAnswersRequest.AnswerItem> answers) {
        User user = findUserByEmail(userEmail);
        treasureHuntRepository.findById(huntId)
                .orElseThrow(() -> new RuntimeException("Treasure hunt not found: " + huntId));
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Step not found: " + stepId));

        if (!step.getTreasureHunt().getId().equals(huntId)) {
            throw new RuntimeException("Step does not belong to this hunt");
        }

        for (SubmitAnswersRequest.AnswerItem item : answers) {
            Question question = questionRepository.findById(item.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + item.getQuestionId()));

            boolean isCorrect = item.getAnswer().trim().toLowerCase()
                    .equals(question.getCorrectAnswer().trim().toLowerCase());

            UserAnswer userAnswer = userAnswerRepository
                    .findFirstByUserIdAndQuestionId(user.getId(), item.getQuestionId())
                    .orElse(new UserAnswer());
            userAnswer.setUser(user);
            userAnswer.setQuestion(question);
            userAnswer.setAnswer(item.getAnswer());
            userAnswer.setIsCorrect(isCorrect);
            userAnswerRepository.save(userAnswer);
        }

        boolean allCorrect = allQuestionsAnsweredCorrectly(user.getId(), stepId);

        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(user.getId(), huntId)
                .orElseThrow(() -> new RuntimeException("No progress found"));

        List<Step> allSteps = stepRepository.findByTreasureHuntIdOrderByStepOrder(huntId);
        boolean isLastStep = allSteps.stream()
                .max(Comparator.comparingInt(Step::getStepOrder))
                .map(s -> s.getId().equals(stepId))
                .orElse(false);

        if (isLastStep) {
            // Dernière étape : déverrouille le trésor seulement si TOUTES les réponses du parcours sont correctes
            if (allHuntQuestionsAnsweredCorrectly(user.getId(), huntId)) {
                progress.setIsTreasureUnlocked(true);
                userProgressRepository.save(progress);
            }
        } else {
            // Étapes intermédiaires : avancer toujours, même si les réponses sont fausses
            progress.setCurrentStep(progress.getCurrentStep() + 1);
            userProgressRepository.save(progress);
        }

        SubmitAnswersResultDTO result = new SubmitAnswersResultDTO();
        result.setAllCorrect(allCorrect);
        return result;
    }

    public ProximityCheckResult checkProximity(String userEmail, Long huntId, Long stepId,
                                               double playerLat, double playerLon) {
        findUserByEmail(userEmail);
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Step not found: " + stepId));

        if (!step.getTreasureHunt().getId().equals(huntId)) {
            throw new RuntimeException("Step does not belong to this hunt");
        }

        int distance = haversineMeters(playerLat, playerLon, step.getLatitude(), step.getLongitude());
        return new ProximityCheckResult(distance <= step.getRadiusMeters(), distance, step.getRadiusMeters());
    }

    public HintDTO getHint(String userEmail, Long huntId, Long stepId) {
        User user = findUserByEmail(userEmail);
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Step not found: " + stepId));

        if (!step.getTreasureHunt().getId().equals(huntId)) {
            throw new RuntimeException("Step does not belong to this hunt");
        }

        List<Long> wrongQuestionIds = questionRepository.findByStepIdOrderByQuestionOrder(stepId).stream()
                .filter(q -> userAnswerRepository
                        .findFirstByUserIdAndQuestionId(user.getId(), q.getId())
                        .map(a -> !a.getIsCorrect())
                        .orElse(true))
                .map(Question::getId)
                .collect(Collectors.toList());

        HintDTO hint = new HintDTO();
        hint.setWrongQuestionIds(wrongQuestionIds);
        return hint;
    }

    public UserProgressDTO getUserProgress(String userEmail, Long huntId) {
        User user = findUserByEmail(userEmail);
        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(user.getId(), huntId)
                .orElseGet(() -> {
                    // Create new progress if it doesn't exist
                    TreasureHunt hunt = treasureHuntRepository.findById(huntId)
                            .orElseThrow(() -> new RuntimeException("Treasure hunt not found"));
                    UserProgress newProgress = new UserProgress();
                    newProgress.setUser(user);
                    newProgress.setTreasureHunt(hunt);
                    newProgress.setCurrentStep(1);
                    newProgress.setIsCompleted(false);
                    newProgress.setIsTreasureUnlocked(false);
                    return userProgressRepository.save(newProgress);
                });
        return mapperService.userProgressToDTO(progress);
    }

    public List<UserProgressDTO> getUserProgresses(String userEmail) {
        User user = findUserByEmail(userEmail);
        return userProgressRepository.findByUserId(user.getId()).stream()
                .map(mapperService::userProgressToDTO)
                .collect(Collectors.toList());
    }

    public CoordinateCalculationService.CalculatedCoordinates calculateTreasureCoordinates(String userEmail, Long huntId) {
        User user = findUserByEmail(userEmail);
        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(user.getId(), huntId)
                .orElseThrow(() -> new RuntimeException("No progress found"));

        if (!progress.getIsTreasureUnlocked()) {
            throw new RuntimeException("Treasure not yet unlocked");
        }

        return coordinateService.calculateCoordinates(user.getId(), progress.getTreasureHunt());
    }

    @Transactional
    public void validateCode(String userEmail, Long huntId, String code) {
        User user = findUserByEmail(userEmail);
        TreasureHunt hunt = treasureHuntRepository.findById(huntId)
                .orElseThrow(() -> new RuntimeException("Treasure hunt not found: " + huntId));
        UserProgress progress = userProgressRepository
                .findByUserIdAndTreasureHuntId(user.getId(), huntId)
                .orElseThrow(() -> new RuntimeException("No progress found"));

        if (!progress.getIsTreasureUnlocked()) {
            throw new RuntimeException("Treasure not yet unlocked");
        }
        if (!code.equals(hunt.getAccessCode())) {
            throw new RuntimeException("Code incorrect");
        }

        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        // Sauvegarder la première complétion (seulement si c'est la première fois)
        if (progress.getFirstCompletedAt() == null) {
            progress.setFirstCompletedAt(LocalDateTime.now());
        }

        userProgressRepository.save(progress);
    }

    private boolean allQuestionsAnsweredCorrectly(Long userId, Long stepId) {
        List<Question> questions = questionRepository.findByStepIdOrderByQuestionOrder(stepId);
        if (questions.isEmpty()) {
            return true;
        }
        return questions.stream().allMatch(q ->
                userAnswerRepository.findFirstByUserIdAndQuestionId(userId, q.getId())
                        .map(UserAnswer::getIsCorrect)
                        .orElse(false));
    }

    private boolean allHuntQuestionsAnsweredCorrectly(Long userId, Long huntId) {
        List<Step> allSteps = stepRepository.findByTreasureHuntIdOrderByStepOrder(huntId);
        return allSteps.stream().allMatch(step -> allQuestionsAnsweredCorrectly(userId, step.getId()));
    }

    private int haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6_371_000; // rayon de la Terre en mètres
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return (int) Math.round(R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
