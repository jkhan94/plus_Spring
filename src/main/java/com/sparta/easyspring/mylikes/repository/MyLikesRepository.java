package com.sparta.easyspring.mylikes.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.easyspring.comment.entity.QComment.comment;
import static com.sparta.easyspring.commentlike.entity.QCommentLike.commentLike;
import static com.sparta.easyspring.post.entity.QPost.post;
import static com.sparta.easyspring.postlike.entity.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class MyLikesRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Post> findAllLikedPosts(long userId, Pageable pageable) {
        List<Post> result = queryFactory.select(post)
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(postLike.user.id.eq(userId))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Post> count = queryFactory.select(post)
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(postLike.user.id.eq(userId))
                .fetch();

        return new PageImpl<>(result, pageable, count.size());
    }

    public Page<Comment> findAllLikeComments(long userId, Pageable pageable) {
        List<Comment> result = queryFactory.select(comment)
                .from(comment)
                .join(commentLike).on(commentLike.comment.eq(comment))
                .where(commentLike.user.id.eq(userId))
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Comment> count = queryFactory.select(comment)
                .from(comment)
                .join(commentLike).on(commentLike.comment.eq(comment))
                .where(commentLike.user.id.eq(userId))
                .fetch();

        return new PageImpl<>(result, pageable, count.size());
    }

    public int countAllLikedPosts(Long userId) {
        return queryFactory.select(post)
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(postLike.user.id.eq(userId))
                .fetch()
                .size();
    }

    public int countAllLikedComments(Long userId) {
        return queryFactory.select(comment)
                .from(comment)
                .join(commentLike).on(commentLike.comment.eq(comment))
                .where(commentLike.user.id.eq(userId))
                .fetch()
                .size();
    }
}
