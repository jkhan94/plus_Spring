package com.sparta.easyspring.post.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.post.dto.PostMediaResponseDto;
import com.sparta.easyspring.post.dto.PostResponseDto;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.entity.PostMedia;
import com.sparta.easyspring.post.repository.PostMediaRepository;
import com.sparta.easyspring.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostMediaService {
    private final PostMediaRepository postMediaRepository;
    private final PostService postService;
    private final S3Service s3Service;

    public Map.Entry<String, String> uploadFiles(Long postId, User user, MultipartFile file) throws IOException {
        Post post = postService.findPostbyId(postId);
        if(!post.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("본인이 업로드한 포스트에만 사진 업로드가 가능합니다.");
        }
        // 파일을 S3에 저장하고 URL과 파일 이름을 받아옴
        String fileUrl = s3Service.saveFile(file).getKey();
        String originalFilename= s3Service.saveFile(file).getValue();

        PostMedia postMedia = new PostMedia(post,user,fileUrl,originalFilename);
        postMediaRepository.save(postMedia);
        // 파일의 URL과 파일 이름을 Map.Entry로 반환
        return new AbstractMap.SimpleEntry<>(fileUrl, originalFilename);
    }

    public void deleteFile(Long postId, User user, String fileName) {
        Post post = postService.findPostbyId(postId);
        if(!post.getUser().equals(user)){
            throw new IllegalArgumentException("본인이 업로드한 파일만 삭제 가능합니다.");
        }
        PostMedia postMedia = postMediaRepository.findByPostAndFilename(post,fileName);
        if(postMedia==null){
            throw new IllegalArgumentException("삭제할 파일이 존재하지 않습니다.");
        }
        postMediaRepository.delete(postMedia);
        // 파일 삭제
        s3Service.deleteImage(fileName);
    }

    public List<PostMediaResponseDto> getAllFiles(Long postId) {
        Post post = postService.findPostbyId(postId);
        List<PostMedia> postMediaList = postMediaRepository.findAllByPost(post);
        List<PostMediaResponseDto> responseDtos = new ArrayList<>();
        for (PostMedia postMedia : postMediaList) {
            PostMediaResponseDto postMediaResponseDto= new PostMediaResponseDto(postMedia);
            responseDtos.add(postMediaResponseDto);
        }
        return responseDtos;
    }
}
