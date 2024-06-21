package com.sparta.easyspring.post.entity;

import com.sparta.easyspring.admin.dto.PostWithStatusRequestDto;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.timestamp.TimeStamp;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.post.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "posts")
@RequiredArgsConstructor
public class Post extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String contents;
    @Column(nullable = false)
    private Long likes;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Comment> commentList = new ArrayList<>();

    private boolean isNotice = false; // 공지글 여부
    private boolean isPinned = false; // 상단 고정 여부

    public Post(PostRequestDto requestDto, User user) {
        this.title= requestDto.getTitle();
        this.contents= requestDto.getContents();
        this.likes=0L;
        this.user=user;
    }

    public void update(PostRequestDto requestDto) {
        this.title= requestDto.getTitle();
        this.contents= requestDto.getContents();
    }

    // 어드민 글 생성시 필요한 생성자
    public Post(PostWithStatusRequestDto requestDto, User user, UserRoleEnum roleEnum){
        if (roleEnum == UserRoleEnum.ADMIN){
            this.title = requestDto.getTitle();
            this.contents= requestDto.getContents();
            this.likes=0L;
            this.user=user;
        }
    }

    public void increaseLikes() {
        this.likes++;
    }

    public void decreaseLikes() {
        this.likes--;
    }

    public void makeNoticePost(boolean isNotice) {
        this.isNotice = isNotice;
    }
    public void makePinPost(boolean isPinned) {
        this.isPinned = isPinned;
    }
}
