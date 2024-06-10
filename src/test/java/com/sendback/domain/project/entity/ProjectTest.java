package com.sendback.domain.project.entity;

import com.sendback.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sendback.domain.project.fixture.ProjectFixture.createDummyProject;
import static com.sendback.domain.user.fixture.UserFixture.createDummyUser;

import static com.sendback.domain.user.fixture.UserFixture.createDummyUser_B;
import static org.assertj.core.api.Assertions.assertThat;


public class ProjectTest {

    @Nested
    @DisplayName("프로젝트 작성자인지 확인")
    class CheckAuthorTest {


        @Test
        @DisplayName("참")
        public void true_checkAuthor() throws Exception {
            //given
            User user = createDummyUser();
            Project project = createDummyProject(user);

            //when
            boolean isAuthor = project.isAuthor(user);

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

            //when
            boolean isAuthor = project.isAuthor(userB);

            //then
            assertThat(isAuthor).isFalse();
        }
    }

    @Nested
    @DisplayName("끌어올릴 수 있는 지 여부")
    class AvailablePullUp {

        @Test
        @DisplayName("참")
        public void true_checkAvailablePullUp() throws Exception {
            //given
            User user = createDummyUser();
            Project project = createDummyProject(user);

            //when
            boolean isAvailablePullUp = project.isAvailablePulledUp();

            //then
            assertThat(isAvailablePullUp).isTrue();
        }

        @Test
        @DisplayName("거짓 (끌어 올린 상태)")
        public void false_alreadyPullUp_checkAvailablePullUp() throws Exception {
            //given
            User user = createDummyUser();
            Project project = createDummyProject(user);
            project.pullUp();

            //when
            boolean isAvailablePullUp = project.isAvailablePulledUp();

            //then
            assertThat(isAvailablePullUp).isFalse();
        }
    }

    @Nested
    @DisplayName("끌어올릴 수 있는 최대를 넘었는지 여부")
    class isOverPullUpCnt {

        @Test
        @DisplayName("참 (project 15번 끌어올린 상태)")
        public void true_checkIsOverPullUpCnt() throws Exception {
            //given
            User user = createDummyUser();
            Project project = createDummyProject(user);
            for (int cnt = 0; cnt < 16; cnt++) {
                project.pullUp();
            }

            //when
            System.out.println(project.getProjectPull().getPullUpCnt());
            boolean isOverPullUpCnt = project.isOverPullUpCnt();

            //then
            assertThat(isOverPullUpCnt).isTrue();
        }

        @Test
        @DisplayName("거짓 (project 15번 이하로 끌어올린 상태)")
        public void false_checkIsOverPullUpCnt() throws Exception {
            //given
            User user = createDummyUser();
            Project project = createDummyProject(user);

            //when
            System.out.println(project.getProjectPull().getPullUpCnt());
            boolean isOverPullUpCnt = project.isOverPullUpCnt();

            //then
            assertThat(isOverPullUpCnt).isFalse();
        }
    }


}
