package com.kbslblog_api.entity;

import com.kbslblog_api.constant.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    @Nationalized
    private String id;

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

    public User(String id, String password, String name, String email, String phoneNumber, UserRole role, String description) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.description = description;
    }
}