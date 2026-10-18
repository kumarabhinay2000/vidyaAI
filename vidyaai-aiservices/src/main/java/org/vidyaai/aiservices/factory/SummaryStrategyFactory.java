package org.vidyaai.aiservices.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.enums.QuizQuestionType;
import org.vidyaai.aiservices.enums.SummaryType;
import org.vidyaai.aiservices.strategy.QuizStrategy;
import org.vidyaai.aiservices.strategy.SummaryStrategy;
import org.vidyaai.aiservices.strategy.SummaryStrategyResolver;

@Component
@RequiredArgsConstructor
@Slf4j
public class SummaryStrategyFactory {

    private final SummaryStrategyResolver strategyResolver;

    public SummaryStrategy getSummaryStrategy(SummaryType type) {
        log.debug("Resolving summary strategy for type: {}", type);
        return strategyResolver.resolveSummary(type);
    }

    public QuizStrategy getQuizStrategy(QuizQuestionType type) {
        log.debug("Resolving quiz strategy for type: {}", type);
        return strategyResolver.resolveQuiz(type);
    }
}