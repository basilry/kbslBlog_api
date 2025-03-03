package com.kbslblog_api.service;

import com.kbslblog_api.dto.post.PostDto;
import com.kbslblog_api.entity.Post;
import com.kbslblog_api.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList())
                .reversed();
    }

    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return convertToDto(post);
    }

    private PostDto convertToDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(post.getPostLikes() != null ? post.getPostLikes().size() : 0)
                .build();
    }
}