package org.vidyaai.aiservices.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.enums.QuizQuestionType;
import org.vidyaai.aiservices.enums.SummaryType;
import org.vidyaai.aiservices.exception.InvalidRequestException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SummaryStrategyResolver {

    private final Map<SummaryType, SummaryStrategy> summaryStrategies =
            new EnumMap<>(SummaryType.class);

    private final Map<QuizQuestionType, QuizStrategy> quizStrategies =
            new EnumMap<>(QuizQuestionType.class);

    public SummaryStrategyResolver(List<SummaryStrategy> summaryList,
                                   List<QuizStrategy> quizList) {
        summaryList.forEach(s -> summaryStrategies.put(s.getSupportedType(), s));
        quizList.forEach(q -> quizStrategies.put(q.getSupportedType(), q));
        log.info("Registered {} summary strategies, {} quiz strategies",
                summaryStrategies.size(), quizStrategies.size());
    }

    public SummaryStrategy resolveSummary(SummaryType type) {
        SummaryStrategy strategy = summaryStrategies.get(type);
        if (strategy == null) {
            throw new InvalidRequestException("Unsupported summary type: " + type);
        }
        return strategy;
    }

    public QuizStrategy resolveQuiz(QuizQuestionType type) {
        QuizStrategy strategy = quizStrategies.get(type);
        if (strategy == null) {
            throw new InvalidRequestException("Unsupported quiz type: " + type);
        }
        return strategy;
    }
}