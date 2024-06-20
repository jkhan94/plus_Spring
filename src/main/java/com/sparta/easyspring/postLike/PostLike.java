package com.sparta.easyspring.postlike;

import com.sparta.easyspring.TimeStamp.TimeStamp;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "postlike")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostLike extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
