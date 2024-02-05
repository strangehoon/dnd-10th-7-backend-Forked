package com.sendback.domain.users.entity;

import com.sendback.global.common.Entity.BaseEntity;
import com.sendback.global.common.constants.Career;
import com.sendback.global.common.constants.Gender;
import com.sendback.global.common.constants.Level;
import com.sendback.global.common.constants.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "isDeleted = false")
@SQLDelete(sql = "UPDATE Users SET isDeleted = true WHERE id = ?")
public class Users extends BaseEntity {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String socialId;
    private String email;
    @Enumerated(EnumType.STRING)
    private Level level;
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String birthDay;
    private String profileImageUrl;
    @Enumerated(EnumType.STRING)
    private Career career;

    private boolean isDeleted = Boolean.FALSE;

    @Builder
    public Users(SocialType socialType, String socialId, String email, Level level, String nickname, Gender gender, String birthday, String profileImageUrl, Career career) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.email = email;
        this.level = level;
        this.nickname = nickname;
        this.gender = gender;
        this.birthDay = birthday;
        this.profileImageUrl = profileImageUrl;
        this.career = career;
    }

    public static Users of(SocialType socialType, String socialId, String email, String nickname, String profileImageUrl){
        return Users.builder()
                .socialType(socialType)
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
