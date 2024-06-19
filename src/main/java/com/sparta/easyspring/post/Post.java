package com.sparta.easyspring.post;

import com.sparta.easyspring.TimeStamp.TimeStamp;
import com.sparta.easyspring.auth.User;
import com.sparta.easyspring.comment.Comment;
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
    @JoinColumn(name = "user_id"/*,nullable = false*/)
    private User user;
    /*@OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Comment> commentList = new ArrayList<>();*/

    public Post(PostRequestDto requestDto) {
        this.title= requestDto.getTitle();
        this.contents= requestDto.getContents();
        this.likes=0L;
    }

    public void update(PostRequestDto requestDto) {
        this.title= requestDto.getTitle();
        this.contents= requestDto.getContents();
    }

    public void increaseLikes() {
        this.likes++;
    }

    public void decreaseLikes() {
        this.likes--;
    }
}
