package com.kbslblog_api.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "thumbnail", columnDefinition = "MEDIUMTEXT")
    private String thumbnail;

    @Lob
    @Nationalized
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // PostLike와의 일대다 관계
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @Builder
    public Post(String title, String content, String thumbnail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        // 현재 시간을 Asia/Seoul 시간대로 명시적 설정
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        // 현재 시간을 Asia/Seoul 시간대로 명시적 설정
        this.updatedAt = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
    }
}