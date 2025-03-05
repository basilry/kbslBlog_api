package com.kbslblog_api.entity;

import com.kbslblog_api.constant.enums.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@Table(name = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Nationalized
    private String loginId;

    @Nationalized
    private String name;

    @Nationalized
    private String password;

    @Nationalized
    private String email;

    @Nationalized
    private String phoneNumber;

    @Nationalized
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String description;

    @Lob
    @Column(name = "profile_img", columnDefinition = "MEDIUMTEXT")
    private String profileImg;


    @Builder
    public User(String loginId, String password, String name, String email, String phoneNumber, UserRole role, String description, String profileImg) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.description = description;
        this.profileImg = profileImg;
    }
}
