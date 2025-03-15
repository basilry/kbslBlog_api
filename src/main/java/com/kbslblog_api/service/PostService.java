package com.kbslblog_api.service;

import com.kbslblog_api.constant.enums.ErrorCode;
import com.kbslblog_api.dto.post.PostDto;
import com.kbslblog_api.dto.post.PostRegisterDto;
import com.kbslblog_api.dto.post.PostUpdateDto;
import com.kbslblog_api.entity.Post;
import com.kbslblog_api.entity.PostLike;
import com.kbslblog_api.exception.AlreadyExistException;
import com.kbslblog_api.exception.NotFoundException;
import com.kbslblog_api.repository.PostLikeRepository;
import com.kbslblog_api.repository.post.PostRepository;
import com.kbslblog_api.repository.post.qdsl.QPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
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
        PostDto result = qPostRepository.findPostById(id);

        if(result == null) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }

        return result;
    }

    public PostDto likePost(Long postId, String clientIp) {
        log.info(clientIp);
        if (postLikeRepository.findByPost_IdAndClientIp(postId, clientIp).isPresent()) {
            // 에러메시지 필요
            throw new AlreadyExistException(ErrorCode.POST_LIKE_ALREADY_EXIST);
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

    // 포스팅 등록 기능: PostRegisterDto를 받아 Post 엔티티 생성 후 저장
    public PostDto registerPost(PostRegisterDto postRegisterDto) {
        Post post = Post.builder()
                .title(postRegisterDto.getTitle())
                .thumbnail(postRegisterDto.getThumbnail())
                .content(postRegisterDto.getContent())
                .build();
        Post savedPost = postRepository.save(post);

        // 저장 후 PostDto로 변환하여 반환 (빌더 패턴 사용)
        return PostDto.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .thumbnail(savedPost.getThumbnail())
                .content(savedPost.getContent())
                .createdAt(savedPost.getCreatedAt())
                .updatedAt(savedPost.getUpdatedAt())
                .likeCount(0L) // 등록 시 초기 좋아요 수 0
                .build();
    }

    // 포스팅 수정(update) 메서드
    public PostDto updatePost(Long postId, PostUpdateDto postUpdateDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        post.setTitle(postUpdateDto.getTitle());
        post.setThumbnail(postUpdateDto.getThumbnail());
        post.setContent(postUpdateDto.getContent());

        Post updatedPost = postRepository.save(post);

        return PostDto.builder()
                .id(updatedPost.getId())
                .title(updatedPost.getTitle())
                .thumbnail(updatedPost.getThumbnail())
                .content(updatedPost.getContent())
                .createdAt(updatedPost.getCreatedAt())
                .updatedAt(updatedPost.getUpdatedAt())
                .likeCount(postLikeRepository.countByPost_Id(updatedPost.getId()))
                .build();
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        postRepository.delete(post);
    }
}