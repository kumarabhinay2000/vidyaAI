package org.vidyaai.quizservices.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vidyaai.quizservices.constant.CacheKeys;
import org.vidyaai.quizservices.constant.RedisTTL;
import org.vidyaai.quizservices.dto.request.CreateQuizRequest;
import org.vidyaai.quizservices.dto.request.UpdateQuizRequest;
import org.vidyaai.quizservices.dto.response.QuizResponse;
import org.vidyaai.quizservices.entity.Quiz;
import org.vidyaai.quizservices.entity.QuizQuestion;
import org.vidyaai.quizservices.exception.QuizNotFoundException;
import org.vidyaai.quizservices.exception.QuizServiceException;
import org.vidyaai.quizservices.mapper.QuizMapper;
import org.vidyaai.quizservices.repository.QuizQuestionRepository;
import org.vidyaai.quizservices.repository.QuizRepository;
import org.vidyaai.quizservices.service.CacheService;
import org.vidyaai.quizservices.service.QuizService;
import org.vidyaai.quizservices.validator.CreateQuizRequestValidator;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizMapper quizMapper;
    private final CacheService cacheService;
    private final CreateQuizRequestValidator createQuizRequestValidator;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public QuizResponse createQuiz(CreateQuizRequest request) {
        createQuizRequestValidator.validate(request);
        log.info("Creating quiz for videoId={}", request.getVideoId());

        try {
            Quiz quiz = Quiz.builder()
                    .videoId(request.getVideoId())
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .difficultyLevel(request.getDifficultyLevel())
                    .passingScore(request.getPassingScore())
                    .timeLimitMinutes(request.getTimeLimitMinutes())
                    .isPublished(Boolean.TRUE.equals(request.getPublish()))
                    .isDeleted(false)
                    .build();
            Quiz saved = quizRepository.save(quiz);

            cacheService.delete(CacheKeys.quizByVideo(saved.getVideoId().toString()));
            log.info("Quiz created: quizId={} videoId={}", saved.getId(), saved.getVideoId());
            return quizMapper.toSummaryResponse(saved);
        } catch (Exception e) {
            log.error("Failed to create quiz for videoId={}: {}",
                    request.getVideoId(), e.getMessage(), e);
            throw new QuizServiceException("Failed to create quiz", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuiz(UUID quizId) {
        try {
            String key = CacheKeys.quiz(quizId.toString());
            Optional<QuizResponse> cached = cacheService.get(key, QuizResponse.class);
            if (cached.isPresent()) {
                return cached.get();
            }

            Quiz quiz = quizRepository.findByIdAndIsDeletedFalse(quizId)
                    .orElseThrow(() -> new QuizNotFoundException(quizId));

            List<QuizQuestion> questions = quizQuestionRepository
                    .findByQuizIdOrderByQuestionOrderAsc(quizId);

            QuizResponse response = quizMapper.toResponse(quiz, questions);
            cacheService.put(key, response, Duration.ofSeconds(RedisTTL.QUIZ_SECONDS));
            return response;
        } catch (QuizNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch quiz {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch quiz", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizForTaking(UUID quizId) {
        try {
            Quiz quiz = quizRepository.findByIdAndIsDeletedFalse(quizId)
                    .orElseThrow(() -> new QuizNotFoundException(quizId));
            if (!quiz.isPublished()) {
                throw new QuizNotFoundException("Quiz is not published: " + quizId);
            }
            List<QuizQuestion> questions = quizQuestionRepository
                    .findByQuizIdOrderByQuestionOrderAsc(quizId);
            return quizMapper.toResponse(quiz, questions);
        } catch (QuizNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch quiz for taking {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch quiz", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizByVideoId(UUID videoId) {
        try {
            String key = CacheKeys.quizByVideo(videoId.toString());
            Optional<QuizResponse> cached = cacheService.get(key, QuizResponse.class);
            if (cached.isPresent()) {
                return cached.get();
            }

            Quiz quiz = quizRepository
                    .findFirstByVideoIdAndIsDeletedFalseOrderByCreatedAtDesc(videoId)
                    .orElseThrow(() -> new QuizNotFoundException(
                            "Quiz not found for videoId: " + videoId));

            List<QuizQuestion> questions = quizQuestionRepository
                    .findByQuizIdOrderByQuestionOrderAsc(quiz.getId());

            QuizResponse response = quizMapper.toResponse(quiz, questions);
            cacheService.put(key, response, Duration.ofSeconds(RedisTTL.QUIZ_SECONDS));
            return response;
        } catch (QuizNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch quiz by videoId {}: {}", videoId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch quiz", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> listUserQuizzes(UUID userId) {
        try {
            List<Quiz> quizzes = quizRepository
                    .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
            return quizzes.stream().map(quizMapper::toSummaryResponse).toList();
        } catch (Exception e) {
            log.error("Failed to list quizzes for user {}: {}", userId, e.getMessage(), e);
            throw new QuizServiceException("Failed to list quizzes", e);
        }
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(UUID quizId, UpdateQuizRequest request) {
        log.info("Updating quiz {}", quizId);
        try {
            Quiz quiz = quizRepository.findByIdAndIsDeletedFalse(quizId)
                    .orElseThrow(() -> new QuizNotFoundException(quizId));

            if (StringUtils.hasText(request.getTitle())) {
                quiz.setTitle(request.getTitle());
            }
            if (StringUtils.hasText(request.getDescription())) {
                quiz.setDescription(request.getDescription());
            }
            if (request.getDifficultyLevel() != null) {
                quiz.setDifficultyLevel(request.getDifficultyLevel());
            }
            if (request.getPassingScore() != null) {
                quiz.setPassingScore(request.getPassingScore());
            }
            if (request.getTimeLimitMinutes() != null) {
                quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
            }
            if (request.getPublished() != null) {
                quiz.setIsPublished(request.getPublished());
            }

            Quiz updated = quizRepository.save(quiz);
            cacheService.delete(CacheKeys.quiz(quizId.toString()));
            cacheService.delete(CacheKeys.quizByVideo(updated.getVideoId().toString()));

            List<QuizQuestion> questions = quizQuestionRepository
                    .findByQuizIdOrderByQuestionOrderAsc(quizId);
            return quizMapper.toResponse(updated, questions);
        } catch (QuizNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update quiz {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to update quiz", e);
        }
    }

    @Override
    @Transactional
    public void deleteQuiz(UUID quizId) {
        try {
            Quiz quiz = quizRepository.findByIdAndIsDeletedFalse(quizId)
                    .orElseThrow(() -> new QuizNotFoundException(quizId));
            quiz.softDelete();
            quizRepository.save(quiz);

            cacheService.delete(CacheKeys.quiz(quizId.toString()));
            cacheService.delete(CacheKeys.quizByVideo(quiz.getVideoId().toString()));
            cacheService.delete(CacheKeys.analytics(quizId.toString()));
            log.info("Quiz soft-deleted: {}", quizId);
        } catch (QuizNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete quiz {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to delete quiz", e);
        }
    }

    @SuppressWarnings("unused")
    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }
}