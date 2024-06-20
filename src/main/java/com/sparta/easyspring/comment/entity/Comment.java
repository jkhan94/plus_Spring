package com.sparta.easyspring.comment.entity;


import com.sparta.easyspring.TimeStamp.TimeStamp;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.comment.dto.CommentRequestDto;
import com.sparta.easyspring.commentLike.CommentLike;
import com.sparta.easyspring.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String comment;
    private Long likes;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    public Comment(User user, Post post, String comment) {
        this.user = user;
        this.post = post;
        this.comment = comment;
        this.likes = 0L;
    }

    public void editComment(CommentRequestDto requestDto) {
        this.comment = requestDto.getComment();
    }

    public void increaseLikes() {
        this.likes += 1L;
    }
    public void decreaseLikes() {
        this.likes -= 1L;
    }
}
