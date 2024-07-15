package com.sendback.domain.feedback.entity;

import com.sendback.domain.project.entity.Project;
import com.sendback.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sendback.domain.feedback.fixture.FeedbackFixture.createDummyFeedback;
import static com.sendback.domain.project.fixture.ProjectFixture.createDummyProject;
import static com.sendback.domain.user.fixture.UserFixture.createDummyUser;
import static com.sendback.domain.user.fixture.UserFixture.createDummyUser_B;
import static org.assertj.core.api.Assertions.assertThat;

public class FeedbackTest {

    @Nested
    @DisplayName("피드백 작성자인지 확인")
    class CheckAuthorTest {

        @Test
        @DisplayName("참")
        public void true_checkAuthor() throws Exception {
            //given
            User user = createDummyUser();
            Project project = createDummyProject(user);
            Feedback feedback = createDummyFeedback(user, project);

            //when
            boolean isAuthor = feedback.isAuthor(user);

            //then
            assertThat(isAuthor).isTrue();
        }

        @Test
        @DisplayName("거짓")
        public void false_checkAuthor() throws Exception {
            //given
            User user = createDummyUser();
            User userB = createDummyUser_B();
            Project project = createDummyProject(user);
            Feedback feedback = createDummyFeedback(user, project);

            //when
            boolean isAuthor = feedback.isAuthor(userB);

            //then
            assertThat(isAuthor).isFalse();
        }
    }
}
