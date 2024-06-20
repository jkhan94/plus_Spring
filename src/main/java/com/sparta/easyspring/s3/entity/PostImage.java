package com.sparta.easyspring.s3.entity;

import com.sparta.easyspring.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "post_image")
@RequiredArgsConstructor
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "file_name")
    private String filename;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
