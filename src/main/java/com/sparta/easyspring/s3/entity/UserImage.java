package com.sparta.easyspring.s3.entity;

import com.sparta.easyspring.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "user_image")
@RequiredArgsConstructor
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "file_name")
    private String filename;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
