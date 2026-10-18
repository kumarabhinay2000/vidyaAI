package org.vidyaai.aiservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vidyaai.aiservices.dto.request.NotesRequest;
import org.vidyaai.aiservices.dto.request.QuizGenerationRequest;
import org.vidyaai.aiservices.dto.request.SummaryRequest;
import org.vidyaai.aiservices.dto.request.TranscriptRequest;
import org.vidyaai.aiservices.dto.response.NotesResponse;
import org.vidyaai.aiservices.dto.response.QuizGenerationResponse;
import org.vidyaai.aiservices.dto.response.SummaryResponse;
import org.vidyaai.aiservices.dto.response.TranscriptResponse;
import org.vidyaai.aiservices.enums.ContentType;
import org.vidyaai.aiservices.enums.Language;
import org.vidyaai.aiservices.enums.QuizDifficulty;
import org.vidyaai.aiservices.enums.QuizQuestionType;
import org.vidyaai.aiservices.enums.SummaryType;
import org.vidyaai.aiservices.event.AiProcessingCompletedEvent;
import org.vidyaai.aiservices.event.AiProcessingFailedEvent;
import org.vidyaai.aiservices.event.AiRegenerateEvent;
import org.vidyaai.aiservices.event.VideoUploadedEvent;
import org.vidyaai.aiservices.producer.AiEventProducer;
import org.vidyaai.aiservices.service.AiOrchestrationService;
import org.vidyaai.aiservices.service.NotesService;
import org.vidyaai.aiservices.service.QuizGenerationService;
import org.vidyaai.aiservices.service.SummaryService;
import org.vidyaai.aiservices.service.TranscriptService;
import org.vidyaai.aiservices.service.VectorStoreService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiOrchestrationServiceImpl implements AiOrchestrationService {

    private final TranscriptService transcriptService;
    private final SummaryService summaryService;
    private final NotesService notesService;
    private final QuizGenerationService quizGenerationService;
    private final VectorStoreService vectorStoreService;
    private final AiEventProducer aiEventProducer;

    @Override
    public void processVideoUploadedEvent(VideoUploadedEvent event) {
        long start = System.currentTimeMillis();
        UUID videoId = event.getVideoId();
        UUID userId = event.getUserId();
        List<String> completed = new ArrayList<>();
        UUID summaryId = null;
        UUID notesId = null;
        UUID quizId = null;

        log.info("Orchestrating AI pipeline for videoId={} userId={}", videoId, userId);

        try {
            // 1. Transcript extraction
            TranscriptResponse transcript = extractTranscriptSafely(event);
            if (transcript == null || transcript.getTranscript() == null
                    || transcript.getTranscript().isBlank()) {
                throw new IllegalStateException("Empty transcript after extraction");
            }

            // 2. Summary
            SummaryResponse summary = summaryService.generateSummary(
                    SummaryRequest.builder()
                            .videoId(videoId)
                            .type(SummaryType.FIVE_POINT)
                            .language(Language.ENGLISH)
                            .useCache(true)
                            .forceRegenerate(false)
                            .build());
            summaryId = summary.getId();
            completed.add(ContentType.SUMMARY.name());

            // 3. Notes
            NotesResponse notes = notesService.generateNotes(
                    NotesRequest.builder()
                            .videoId(videoId)
                            .language(Language.ENGLISH)
                            .includeTimestamps(true)
                            .useCache(true)
                            .forceRegenerate(false)
                            .build());
            notesId = notes.getId();
            completed.add(ContentType.NOTES.name());

            // 4. Quiz
            QuizGenerationResponse quiz = quizGenerationService.generateQuiz(
                    QuizGenerationRequest.builder()
                            .videoId(videoId)
                            .questionType(QuizQuestionType.MCQ)
                            .difficulty(QuizDifficulty.MEDIUM)
                            .numberOfQuestions(5)
                            .language(Language.ENGLISH)
                            .useCache(true)
                            .forceRegenerate(false)
                            .build());
            quizId = quiz.getId();
            completed.add(ContentType.QUIZ.name());

            // 5. Embeddings (async in vector store impl)
            vectorStoreService.storeEmbeddings(videoId, transcript.getTranscript());
            completed.add("EMBEDDINGS");

            long elapsed = System.currentTimeMillis() - start;

            AiProcessingCompletedEvent completedEvent = AiProcessingCompletedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .videoId(videoId)
                    .userId(userId)
                    .contentTypes(completed)
                    .summaryId(summaryId)
                    .notesId(notesId)
                    .quizId(quizId)
                    .processingTimeMs(elapsed)
                    .cacheHit(false)
                    .completedAt(LocalDateTime.now())
                    .build();
            aiEventProducer.publishProcessingCompleted(completedEvent);

            log.info("AI pipeline COMPLETED for videoId={} in {}ms, contentTypes={}",
                    videoId, elapsed, completed);

        } catch (Exception e) {
            log.error("AI pipeline FAILED for videoId={}: {}", videoId, e.getMessage(), e);
            publishFailure(videoId, userId, "PIPELINE", e);
        }
    }

    @Override
    public void processRegenerateEvent(AiRegenerateEvent event) {
        UUID videoId = event.getVideoId();
        UUID userId = event.getUserId();
        log.info("Regenerate requested for videoId={} contentType={}",
                videoId, event.getContentType());

        try {
            ContentType type = ContentType.valueOf(event.getContentType().toUpperCase());

            switch (type) {
                case SUMMARY -> summaryService.generateSummary(
                        SummaryRequest.builder()
                                .videoId(videoId)
                                .type(SummaryType.FIVE_POINT)
                                .language(Language.ENGLISH)
                                .useCache(false)
                                .forceRegenerate(true)
                                .build());

                case NOTES -> notesService.generateNotes(
                        NotesRequest.builder()
                                .videoId(videoId)
                                .language(Language.ENGLISH)
                                .includeTimestamps(true)
                                .useCache(false)
                                .forceRegenerate(true)
                                .build());

                case QUIZ -> quizGenerationService.generateQuiz(
                        QuizGenerationRequest.builder()
                                .videoId(videoId)
                                .questionType(QuizQuestionType.MCQ)
                                .difficulty(QuizDifficulty.MEDIUM)
                                .numberOfQuestions(5)
                                .language(Language.ENGLISH)
                                .useCache(false)
                                .forceRegenerate(true)
                                .build());

                default -> throw new IllegalArgumentException(
                        "Unsupported content type for regeneration: " + type);
            }

            AiProcessingCompletedEvent completedEvent = AiProcessingCompletedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .videoId(videoId)
                    .userId(userId)
                    .contentTypes(List.of(type.name()))
                    .processingTimeMs(0L)
                    .cacheHit(false)
                    .completedAt(LocalDateTime.now())
                    .build();
            aiEventProducer.publishProcessingCompleted(completedEvent);

            log.info("Regenerated {} for videoId={}", type, videoId);

        } catch (Exception e) {
            log.error("Regeneration failed for videoId={}: {}", videoId, e.getMessage(), e);
            publishFailure(videoId, userId, "REGENERATE", e);
        }
    }

    private TranscriptResponse extractTranscriptSafely(VideoUploadedEvent event) {
        try {
            TranscriptRequest req = TranscriptRequest.builder()
                    .videoId(event.getVideoId())
                    .sourceUrl(event.getSourceUrl())
                    .forceRegenerate(false)
                    .build();
            return transcriptService.extractTranscript(req);
        } catch (Exception e) {
            log.error("Transcript extraction failed for videoId={}: {}",
                    event.getVideoId(), e.getMessage(), e);
            return null;
        }
    }

    private void publishFailure(UUID videoId, UUID userId, String step, Exception e) {
        try {
            AiProcessingFailedEvent failed = AiProcessingFailedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .videoId(videoId)
                    .userId(userId)
                    .errorCode("AI_999")
                    .errorMessage(e.getMessage())
                    .step(step)
                    .retryCount(0)
                    .failedAt(LocalDateTime.now())
                    .build();
            aiEventProducer.publishProcessingFailed(failed);
        } catch (Exception ex) {
            log.error("Failed to publish AI failure event: {}", ex.getMessage());
        }
    }
}