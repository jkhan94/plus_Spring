package com.sparta.easyspring.post.repository;

import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia,Long> {
    PostMedia findByPostAndFilename(Post post, String fileName);

    List<PostMedia> findAllByPost(Post post);
}
