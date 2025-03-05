package com.kbslblog_api.service;

import com.kbslblog_api.dto.post.PostDto;
import com.kbslblog_api.repository.post.qdsl.QPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final QPostRepository qPostRepository;

    public Page<PostDto> getAllPosts(Pageable pageable) {
        return qPostRepository.findAllPosts(pageable);
    }

    public PostDto getPostById(Long id) {
        return qPostRepository.findPostById(id);
    }
}