package com.sendback.domain.project.fixture;

import com.sendback.domain.field.entity.Field;
import com.sendback.domain.project.dto.request.SaveProjectRequestDto;
import com.sendback.domain.project.dto.request.UpdateProjectRequestDto;
import com.sendback.global.common.constants.Progress;
import com.sendback.domain.project.entity.Project;
import com.sendback.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

public class ProjectFixture {

    private static final String TITLE = "title";
    private static final String UPDATE_TITLE = "update_title";
    private static final String FIELD = "edu";
    private static final String UPDATE_FIELD = "art";
    private static final String CONTENT = "content";
    private static final String UPDATE_CONTENT = "update content";
    private static final String SUMMARY = "summary";
    private static final String UPDATE_SUMMARY = "update summary";
    private static final String DEMO_SITE_URL = "demoUrl";
    private static final String UPDATE_DEMO_SITE_URL = "update demoUrl";
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate UPDATE_START_DATE = LocalDate.of(2024, 2, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 1, 3);
    private static final LocalDate UPDATE_END_DATE = LocalDate.of(2024, 2, 3);
    private static final Progress PLANNING_PROGRESS = Progress.PLANNING;
    private static final Progress DEVELOPING_PROGRESS = Progress.DEVELOPING;

    public static final SaveProjectRequestDto MOCK___SAVE_PROJECT_REQUEST_DTO = new SaveProjectRequestDto(TITLE, FIELD, CONTENT, SUMMARY, DEMO_SITE_URL, START_DATE,
            END_DATE, PLANNING_PROGRESS.toString(), 1L,2L,3L,4L);

    public static final UpdateProjectRequestDto MOCK___UPDATE_PROJECT_REQUEST_DTO = new UpdateProjectRequestDto(
            UPDATE_TITLE, UPDATE_FIELD, UPDATE_CONTENT, UPDATE_SUMMARY, UPDATE_DEMO_SITE_URL, UPDATE_START_DATE, UPDATE_END_DATE, DEVELOPING_PROGRESS.toString(),
            4L, 3L, 2L, 1L, List.of("deleteUrl")
    );

    public static Project createDummyProject(User user) {
        return Project.of(user, Field.of(FIELD), MOCK___SAVE_PROJECT_REQUEST_DTO);
    }

}
