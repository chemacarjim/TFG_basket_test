package com.chema.backend.mapper;

import com.chema.backend.domain.entity.Question;
import com.chema.backend.domain.entity.TestEntity;
import com.chema.backend.dto.QuestionDto;
import com.chema.backend.dto.TestDetailDto;
import com.chema.backend.dto.TestSummaryDto;

import java.util.List;

public class TestMapper {

    public static TestSummaryDto toSummary(TestEntity e) {
        return new TestSummaryDto(
                e.getId(),
                e.getTitle(),
                e.getDescription()
        );
    }

    public static QuestionDto toQuestionDto(Question q) {
        return new QuestionDto(
                q.getId(),
                q.getPrompt(),
                q.getPossessionTime(),
                q.getImageUrl(),
                q.getOrderIndex()
        );
    }

    public static TestDetailDto toDetail(TestEntity e, List<Question> questionsSorted) {
        var qDtos = questionsSorted.stream().map(TestMapper::toQuestionDto).toList();
        return new TestDetailDto(
                e.getId(),
                e.getTitle(),
                qDtos
        );
        }
}
