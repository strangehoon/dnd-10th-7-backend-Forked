package com.sendback.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sendback.domain.project.entity.Project;
import java.time.LocalDateTime;

public record RecommendedProjectResponseDto (
    Long projectId,
    String progress,
    String field,
    String title,
    String summary,
    String createdBy,
    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDateTime createdAt,
    String profileImageUrl
){
    public static RecommendedProjectResponseDto of(Project project) {
        return new RecommendedProjectResponseDto(
                project.getId(),
                project.getSummary(),
                project.getUser().getNickname(),
                project.getProgress().getValue(),
                project.getFieldName().getName(),
                project.getTitle(),
                project.getCreatedAt(),
                project.getUser().getProfileImageUrl()
        );
    }
}