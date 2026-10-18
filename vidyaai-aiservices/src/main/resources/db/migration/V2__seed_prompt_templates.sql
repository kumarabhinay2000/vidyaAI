-- =========================================================
-- Seed base prompt templates
-- =========================================================

INSERT INTO prompt_templates (name, category, template, version, is_active, description)
VALUES
    ('summary_5_point', 'SUMMARY',
     'You are an expert summarizer.
    Extract exactly 5 key points from the following video transcript.
    Each point should be 1-2 sentences, clear and concise.

    Transcript:
    {content}

    Respond in {language}.

    Return ONLY valid JSON matching this schema:
    {
      "title": "string",
      "points": ["string", "string", "string", "string", "string"],
      "keywords": ["string"]
    }', 1, TRUE, 'Five point summary'),

    ('summary_10_point', 'SUMMARY',
     'You are an expert summarizer.
    Extract exactly 10 key points from the following video transcript.

    Transcript:
    {content}

    Respond in {language}.

    Return ONLY valid JSON:
    {
      "title": "string",
      "points": ["string"],
      "keywords": ["string"]
    }', 1, TRUE, 'Ten point summary'),

    ('summary_detailed', 'SUMMARY',
     'You are an expert summarizer.
    Produce a detailed, structured summary of the following video transcript.

    Transcript:
    {content}

    Respond in {language}.

    Return ONLY valid JSON:
    {
      "title": "string",
      "points": ["string"],
      "keywords": ["string"]
    }', 1, TRUE, 'Detailed summary'),

    ('quiz_mcq', 'QUIZ',
     'Generate {numberOfQuestions} multiple-choice questions from the transcript.
    Difficulty: {difficulty}.
    Each question must have exactly 4 options and one correct answer.
    Include a short explanation.

    Transcript:
    {content}

    Respond in {language}.

    Return ONLY valid JSON:
    {
      "title": "string",
      "questions": [
        {
          "index": 1,
          "question": "string",
          "options": ["A", "B", "C", "D"],
          "correctAnswer": "string",
          "explanation": "string",
          "marks": 1
        }
      ]
    }', 1, TRUE, 'MCQ quiz'),

    ('quiz_true_false', 'QUIZ',
     'Generate {numberOfQuestions} true/false questions from the transcript.
    Difficulty: {difficulty}.

    Transcript:
    {content}

    Respond in {language}.

    Return ONLY valid JSON:
    {
      "title": "string",
      "questions": [
        {
          "index": 1,
          "question": "string",
          "options": ["True", "False"],
          "correctAnswer": "True",
          "explanation": "string",
          "marks": 1
        }
      ]
    }', 1, TRUE, 'True/False quiz'),

    ('quiz_fill_blank', 'QUIZ',
     'Generate {numberOfQuestions} fill-in-the-blank questions from the transcript.
    Difficulty: {difficulty}.

    Transcript:
    {content}

    Respond in {language}.

    Return ONLY valid JSON:
    {
      "title": "string",
      "questions": [
        {
          "index": 1,
          "question": "The _____ is responsible for ...",
          "options": [],
          "correctAnswer": "string",
          "explanation": "string",
          "marks": 1
        }
      ]
    }', 1, TRUE, 'Fill-in-the-blank quiz'),

    ('notes_bullet', 'NOTES',
     'Create structured notes for the following video transcript.
    Group content under meaningful headings with bullet points.
    Include timestamps if includeTimestamps is true.

    includeTimestamps: {includeTimestamps}

    Transcript:
    {content}

    Respond in {language}.

    Return ONLY valid JSON:
    {
      "title": "string",
      "sections": [
        {
          "heading": "string",
          "bulletPoints": ["string"],
          "startTimeSeconds": 0
        }
      ]
    }', 1, TRUE, 'Bullet notes'),

    ('chatbot_rag', 'CHATBOT',
     'You are VidyaAI Tutor.
    Answer the student''s question using ONLY the context provided.
    If the answer is not in the context, say you do not have information about this in the video.
    Be concise, helpful, and educational.

    Context:
    {context}

    Previous conversation:
    {chatHistory}

    Student question: {question}

    Respond in {language}.', 1, TRUE, 'RAG chatbot'),

    ('multilingual', 'TRANSLATION',
     'Translate the following content into {language}. Preserve formatting.

    Content:
    {content}', 1, TRUE, 'Multilingual translation');

-- =========================================================
-- Legacy flat prompt files used by PromptTemplateService
-- for the file-based loader fallback are kept under
-- src/main/resources/prompts/*.txt
-- =========================================================