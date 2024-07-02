package com.sparta.easyspring.mylikes.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.post.entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.easyspring.comment.entity.QComment.comment;
import static com.sparta.easyspring.commentlike.entity.QCommentLike.commentLike;
import static com.sparta.easyspring.post.entity.QPost.post;
import static com.sparta.easyspring.postlike.entity.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class MyLikesRepository {
    @PersistenceContext
    EntityManager em;

    private final JPAQueryFactory queryFactory;

 /*   public List<PostQueryResponseDto> findAllDtoByInterestsMemberId(Long memberId, Pageable pageable){
        JPQLQuery<PostQueryResponseDto> query = from(post)
                .select(Projections.constructor(PostQueryResponseDto.class,
                        post.id, post.title, post.content, post.link, post.writer, post.type,
                        post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
                .join(post.category, category)
                .join(category.interests, interest)
                .where(interest.member.id.eq(memberId));
        addSorting(pageable.getSort(), query);
        return getPagingResults(pageable, query);
    }

    List<Person> userList = jpf
            .selectFrom(user)
            .where(person.username.eq(username)
                    .and(person.password.eq(password))
                    .fetch();*/

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

        return new PageImpl<>(result,pageable, count.size());
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

        return new PageImpl<>(result,pageable, count.size());
    }
}
