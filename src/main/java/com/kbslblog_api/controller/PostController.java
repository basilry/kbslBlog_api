package com.kbslblog_api.controller;

import com.kbslblog_api.constant.Constants;
import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.dto.post.PostDto;
import com.kbslblog_api.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/posts")
public class PostController {


    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping
    public ResponseEntity<ApiResult> getAllPosts(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        ApiResult result = new ApiResult();

        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostDto> posts = postService.getAllPosts(pageable);

        result.setData(posts);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult> getPostById(@PathVariable("id") Long id) {
        ApiResult result = new ApiResult();

        PostDto post = postService.getPostById(id);

        result.setData(post);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResult> likePost(@PathVariable("id") Long id, HttpServletRequest request) {
        ApiResult result = new ApiResult();

        String clientIp = request.getRemoteAddr();
        PostDto updated = postService.likePost(id, clientIp);

        result.setData(updated);

        return ResponseEntity.ok(result);
    }
}