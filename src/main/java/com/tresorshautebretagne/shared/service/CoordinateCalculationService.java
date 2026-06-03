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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calcule les coordonnées finales d'une chasse à partir des réponses du joueur.
 *
 * <p>Chaque chasse porte sa propre formule ({@link TreasureHunt#getCoordinateFormula()}),
 * exprimée en DMS (minutes décimales) avec des tokens à substituer par les réponses :
 * <pre>N 47°4(B).2(D)(Bx2)' / W 1°(D)0.(C)(A+1)0'</pre>
 * Les variables A, B, C, D... correspondent, dans l'ordre, aux réponses numériques correctes
 * des énigmes (ordre des étapes puis des questions). Si la chasse n'a pas de formule, on
 * renvoie directement les coordonnées finales stockées.
 */
@Service
@RequiredArgsConstructor
public class CoordinateCalculationService {

    private final UserAnswerRepository userAnswerRepository;
    private final StepRepository stepRepository;
    private final QuestionRepository questionRepository;

    // Token de formule : (A), (Bx2), (A+1), (C-1)... -> variable + opérateur optionnel + nombre
    private static final Pattern TOKEN = Pattern.compile("\\(([A-Z])(?:\\s*([x*+\\-])\\s*(\\d+))?\\)");
    // Une coordonnée DMS en minutes décimales : N 47°43.256'
    private static final Pattern DMS = Pattern.compile("([NSEW])\\s*(\\d+)\\s*°\\s*([0-9]+(?:\\.[0-9]+)?)\\s*'");

    public CalculatedCoordinates calculateCoordinates(Long userId, TreasureHunt hunt) {
        String formula = hunt.getCoordinateFormula();

        // Pas de formule pour cette chasse : on renvoie les coordonnées finales stockées.
        if (formula == null || formula.isBlank()) {
            return new CalculatedCoordinates(hunt.getFinalLatitude(), hunt.getFinalLongitude());
        }

        Map<Character, Integer> values = extractAnswerValues(userId, hunt);

        try {
            String resolved = substituteTokens(formula, values);
            return parseDms(resolved);
        } catch (RuntimeException e) {
            // Formule invalide ou variable manquante : repli sur les coordonnées stockées.
            return new CalculatedCoordinates(hunt.getFinalLatitude(), hunt.getFinalLongitude());
        }
    }

    /**
     * Associe A, B, C, D... aux réponses correctes (numériques),
     * dans l'ordre des étapes puis des questions.
     */
    private Map<Character, Integer> extractAnswerValues(Long userId, TreasureHunt hunt) {
        Map<Character, Integer> values = new HashMap<>();
        char label = 'A';

        List<Step> steps = stepRepository.findByTreasureHuntIdOrderByStepOrder(hunt.getId());
        for (Step step : steps) {
            List<Question> questions = questionRepository.findByStepIdOrderByQuestionOrder(step.getId());
            for (Question question : questions) {
                List<UserAnswer> answers = userAnswerRepository.findByUserIdAndQuestionId(userId, question.getId());
                if (!answers.isEmpty() && Boolean.TRUE.equals(answers.get(0).getIsCorrect())) {
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
        if (answer == null) {
            return null;
        }
        try {
            return Integer.parseInt(answer.trim());
        } catch (NumberFormatException e) {
            String digits = answer.replaceAll("[^0-9]", "");
            return digits.isEmpty() ? null : Integer.parseInt(digits);
        }
    }

    /**
     * Remplace chaque token (A), (Bx2), (A+1)... par sa valeur numérique calculée.
     */
    private String substituteTokens(String formula, Map<Character, Integer> values) {
        Matcher m = TOKEN.matcher(formula);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            char var = m.group(1).charAt(0);
            Integer value = values.get(var);
            if (value == null) {
                throw new IllegalStateException("Valeur manquante pour la variable " + var);
            }
            int result = value;
            if (m.group(2) != null) {
                int operand = Integer.parseInt(m.group(3));
                result = switch (m.group(2)) {
                    case "x", "*" -> value * operand;
                    case "+" -> value + operand;
                    case "-" -> value - operand;
                    default -> value;
                };
            }
            m.appendReplacement(out, Integer.toString(result));
        }
        m.appendTail(out);
        return out.toString();
    }

    /**
     * Convertit une formule résolue "N 47°43.256' / W 1°50.980'" en coordonnées décimales
     * (degrés + minutes / 60, signe négatif pour S et W).
     */
    private CalculatedCoordinates parseDms(String resolved) {
        Matcher m = DMS.matcher(resolved);
        Double latitude = null;
        Double longitude = null;

        while (m.find()) {
            String hemisphere = m.group(1);
            int degrees = Integer.parseInt(m.group(2));
            double minutes = Double.parseDouble(m.group(3));
            double decimal = degrees + minutes / 60.0;
            if (hemisphere.equals("S") || hemisphere.equals("W")) {
                decimal = -decimal;
            }
            decimal = Math.round(decimal * 1_000_000.0) / 1_000_000.0;

            if (hemisphere.equals("N") || hemisphere.equals("S")) {
                latitude = decimal;
            } else {
                longitude = decimal;
            }
        }

        if (latitude == null || longitude == null) {
            throw new IllegalStateException("Coordonnées DMS incomplètes : " + resolved);
        }
        return new CalculatedCoordinates(latitude, longitude);
    }

    @Data
    @AllArgsConstructor
    public static class CalculatedCoordinates {
        private Double latitude;
        private Double longitude;
    }
}
