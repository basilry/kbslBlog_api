package com.kbslblog_api.repository;

import com.kbslblog_api.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPost_IdAndClientIp(Long postId, String clientIp);
    Long countByPost_Id(Long postId);
}