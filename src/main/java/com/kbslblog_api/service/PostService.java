package com.kbslblog_api.service;

import com.kbslblog_api.constant.enums.ErrorCode;
import com.kbslblog_api.dto.post.PostDto;
import com.kbslblog_api.entity.Post;
import com.kbslblog_api.entity.PostLike;
import com.kbslblog_api.exception.NotFoundException;
import com.kbslblog_api.repository.PostLikeRepository;
import com.kbslblog_api.repository.post.PostRepository;
import com.kbslblog_api.repository.post.qdsl.QPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final QPostRepository qPostRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public Page<PostDto> getAllPosts(Pageable pageable) {
        return qPostRepository.findAllPosts(pageable);
    }

    public PostDto getPostById(Long id) {
        return qPostRepository.findPostById(id);
    }

    public PostDto likePost(Long postId, String clientIp) {
        if (postLikeRepository.findByPost_IdAndClientIp(postId, clientIp).isPresent()) {
            return getPostById(postId);
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        PostLike postLike = PostLike.builder()
                .post(post)
                .clientIp(clientIp)
                .build();
        postLikeRepository.save(postLike);
        return getPostById(postId);
    }
}