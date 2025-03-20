package com.kbslblog_api.entity;

import com.kbslblog_api.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "client_ip"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요 대상 포스트
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 좋아요를 누른 클라이언트의 IP 주소
    @Column(name = "client_ip", nullable = false, length = 45)
    private String clientIp;

    // 좋아요가 기록된 시간
    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void prePersist() {
        // 중앙화된 시간 유틸리티 사용
        this.likedAt = DateTimeUtil.nowInSeoul();
    }
}