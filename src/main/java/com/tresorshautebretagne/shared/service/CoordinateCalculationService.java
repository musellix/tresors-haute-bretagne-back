package com.tresorshautebretagne.shared.service;

import com.tresorshautebretagne.treasureHunt.TreasureHunt;
import com.tresorshautebretagne.treasureHunt.question.Question;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.treasureHunt.step.StepRepository;
import com.tresorshautebretagne.treasureHunt.question.QuestionRepository;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswerRepository;
import com.tresorshautebretagne.userProgress.userAnswer.UserAnswer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoordinateCalculationService {

    private final UserAnswerRepository userAnswerRepository;
    private final StepRepository stepRepository;
    private final QuestionRepository questionRepository;

    public CalculatedCoordinates calculateCoordinates(Long userId, TreasureHunt hunt) {
        List<Step> steps = stepRepository.findByTreasureHuntIdOrderByStepOrder(hunt.getId());
        Map<Character, Integer> answerValues = extractAnswerValues(userId, steps);
        return applyCoordinateFormula(answerValues, hunt);
    }

    private Map<Character, Integer> extractAnswerValues(Long userId, List<Step> steps) {
        Map<Character, Integer> values = new HashMap<>();
        char label = 'A';
        
        for (Step step : steps) {
            List<Question> questions = questionRepository.findByStepIdOrderByQuestionOrder(step.getId());
            
            for (Question question : questions) {
                List<UserAnswer> answers = userAnswerRepository.findByUserIdAndQuestionId(userId, question.getId());
                
                if (!answers.isEmpty() && answers.get(0).getIsCorrect()) {
                    Integer numericValue = extractNumericValue(answers.get(0).getAnswer());
                    if (numericValue != null) {
                        values.put(label, numericValue);
                        label++;
                    }
                }
            }
        }
        
        return values;
    }

    private Integer extractNumericValue(String answer) {
        try {
            return Integer.parseInt(answer.trim());
        } catch (NumberFormatException e) {
            String numbers = answer.replaceAll("[^0-9]", "");
            if (!numbers.isEmpty()) {
                return Integer.parseInt(numbers.substring(0, 1));
            }
            return null;
        }
    }

    private CalculatedCoordinates applyCoordinateFormula(Map<Character, Integer> values, TreasureHunt hunt) {
        Integer a = values.getOrDefault('A', 0);
        Integer b = values.getOrDefault('B', 0);
        Integer c = values.getOrDefault('C', 0);
        Integer d = values.getOrDefault('D', 0);
        
        String latDegrees = "47";
        String latMinutes = "4" + b + ".2" + d + (b * 2);
        Double latitude = Double.parseDouble(latDegrees + (latMinutes.length() > 0 ? "." + latMinutes : ""));
        
        String lonDegrees = "1";
        String lonMinutes = d + "0." + c + (a + 1) + "0";
        Double longitude = -(Double.parseDouble(lonDegrees + (lonMinutes.length() > 0 ? "." + lonMinutes : "")));
        
        latitude = Math.round(latitude * 1000000.0) / 1000000.0;
        longitude = Math.round(longitude * 1000000.0) / 1000000.0;
        
        return new CalculatedCoordinates(latitude, longitude);
    }

    @Data
    @AllArgsConstructor
    public static class CalculatedCoordinates {
        private Double latitude;
        private Double longitude;
    }
}
