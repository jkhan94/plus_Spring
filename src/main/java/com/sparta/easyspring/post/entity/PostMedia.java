package com.sparta.easyspring.post.entity;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "post_image")
@RequiredArgsConstructor
public class PostMedia extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "file_name")
    private String filename;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PostMedia(Post post, User user, String fileUrl, String originalFilename) {
        this.post=post;
        this.user=user;
        this.imageUrl=fileUrl;
        this.filename=originalFilename;
    }
}
