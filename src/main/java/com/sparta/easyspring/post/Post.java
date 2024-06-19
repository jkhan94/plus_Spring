package com.sparta.easyspring.post;

import com.sparta.easyspring.TimeStamp.TimeStamp;
import com.sparta.easyspring.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    @JoinColumn(name = "user_id"/*,nullable = false*/)
    private User user;

    public Post(PostRequestDto requestDto) {
        this.title= requestDto.getTitle();
        this.contents= requestDto.getContents();
        this.likes=0L;
    }
}
