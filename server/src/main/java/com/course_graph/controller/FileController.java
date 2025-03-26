package com.course_graph.controller;

import com.course_graph.dto.CommonResponse;
import com.course_graph.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/api/file-upload")
    public ResponseEntity<CommonResponse> join(@RequestBody MultipartFile file, Authentication auth) {
        fileService.upload(file);
        return new ResponseEntity<>(new CommonResponse("파일이 업로드되었습니다."), HttpStatus.CREATED);
    }
}
