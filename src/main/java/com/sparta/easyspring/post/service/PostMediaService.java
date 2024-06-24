package com.sparta.easyspring.post.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.post.dto.PostMediaResponseDto;
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

import static com.sparta.easyspring.exception.ErrorEnum.*;

@Service
@RequiredArgsConstructor
public class PostMediaService {
    private final PostMediaRepository postMediaRepository;
    private final PostService postService;
    private final S3Service s3Service;

    public Map.Entry<String, String> uploadFiles(Long postId, User user, MultipartFile file) throws IOException {
        Post post = postService.findPostbyId(postId);
        if(!post.getUser().getId().equals(user.getId())){
            throw new CustomException(INCORRECT_USER);
        }
        long countMedia = postMediaRepository.countByPost(post);
        if(countMedia==5 || countMedia>5){
            throw new CustomException(EXCEED_MAX_COUNT);
        }
        validateFile(file);
        // 파일을 S3에 저장하고 URL과 파일 이름을 받아옴
        String fileUrl = s3Service.saveFile(file).getKey();
        String originalFilename= s3Service.saveFile(file).getValue();

        PostMedia postMedia = new PostMedia(post,user,fileUrl,originalFilename);
        postMediaRepository.save(postMedia);

        return new AbstractMap.SimpleEntry<>(fileUrl, originalFilename);
    }

    public void deleteFile(Long postId, User user, Long fileId) {
        Post post = postService.findPostbyId(postId);
        if(!post.getUser().getId().equals(user.getId())){
            throw new CustomException(INCORRECT_USER);
        }
        PostMedia postMedia = postMediaRepository.findByIdAndPost(fileId,post);
        if(postMedia==null){
            throw new CustomException(NON_EXISTENT_ELEMENT);
        }
        postMediaRepository.delete(postMedia);
        s3Service.deleteImage(postMedia.getFilename());
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
    private void validateFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String fileExtension = getFileExtension(filename).toLowerCase();
        long fileSize = file.getSize();

        // 이미지 파일 형식 및 크기 제한
        if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
            if (fileSize > 10 * 1024 * 1024) { // 10MB 제한
                throw new CustomException(NOT_ALLOW_IMAGE_SIZE);
            }
        }
        // 비디오 및 GIF 파일 형식 및 크기 제한
        else if (fileExtension.equals("mp4") || fileExtension.equals("avi") || fileExtension.equals("gif")) {
            if (fileSize > 200 * 1024 * 1024) { // 200MB 제한
                throw new CustomException(NOT_ALLOW_VIDEO_SIZE);
            }
        } else {
            throw new CustomException(NOT_ALLOW_FORMAT);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new CustomException(INCORRECT_FILE_NAME);
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            throw new CustomException(INCORRECT_EXTENSION);
        }
        return filename.substring(dotIndex + 1);
    }
}
