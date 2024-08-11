package com.sendback.domain.comment.fixture;

import com.sendback.domain.comment.entity.Comment;
import com.sendback.domain.project.entity.Project;
import com.sendback.domain.user.entity.User;

public class CommentFixture {

    public static Comment createDummyComment(User user, Project project){
        return Comment.of("안녕", user, project);
    }
}
