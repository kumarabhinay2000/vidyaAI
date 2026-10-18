package org.vidyaai.quizservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.quizservices.enums.QuestionType;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private UUID id;
    private QuestionType questionType;
    private String questionText;
    private List<String> options;
    private Integer marks;
    private Integer questionOrder;
}