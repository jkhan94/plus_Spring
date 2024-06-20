package com.sparta.easyspring.comment.entity;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.comment.dto.CommentRequestDto;
import com.sparta.easyspring.commentlike.entity.CommentLike;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.timestamp.TimeStamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
