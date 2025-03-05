package com.kbslblog_api.repository.post.qdsl;

import com.kbslblog_api.dto.post.PostDto;
import com.kbslblog_api.entity.Post;
import com.kbslblog_api.entity.QPost;
import com.kbslblog_api.entity.QPostLike;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QPostRepository {

    private final JPAQueryFactory queryFactory;

    QPost post = QPost.post;
    QPostLike postLike = QPostLike.postLike;

    public Page<PostDto> findAllPosts(Pageable pageable) {
        List<PostDto> content = queryFactory
                .select(Projections.constructor(
                        PostDto.class,
                        post.id,
                        post.title,
                        post.content,
                        post.createdAt,
                        post.updatedAt,
                        // ExpressionUtils.as 없이, 서브쿼리 결과를 바로 전달합니다.
                        queryFactory.select(postLike.count())
                                .from(postLike)
                                .where(postLike.post.eq(post))
                ))
                .from(post)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, total::longValue);
    }

    public PostDto findPostById(Long id) {
        PostDto result = queryFactory
                .select(Projections.constructor(
                        PostDto.class,
                        post.id,
                        post.title,
                        post.content,
                        post.createdAt,
                        post.updatedAt,
                        queryFactory.select(postLike.count())
                                .from(postLike)
                                .where(postLike.post.eq(post))
                ))
                .from(post)
                .where(post.id.eq(id))
                .fetchOne();
        return result;
    }
}