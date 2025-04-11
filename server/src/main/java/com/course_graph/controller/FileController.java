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

    @PostMapping("/api/history-upload")
    public ResponseEntity<CommonResponse> historyFileUpload(@RequestBody MultipartFile file, Authentication auth) {
        fileService.historyFileUpload(file, auth.getName());
        return new ResponseEntity<>(new CommonResponse("파일이 업로드되었습니다."), HttpStatus.CREATED);
    }

//    // 편의상 db 초기화를 위해 선언해둠. 추후 db 초기화 파일로 대체 예정
//    @PostMapping("/api/subject-upload")
//    public ResponseEntity<CommonResponse> subjectFileUpload(@RequestBody MultipartFile file, int year) {
//        fileService.subjectFileUpload(file, year);
//        return new ResponseEntity<>(new CommonResponse("파일이 업로드되었습니다."), HttpStatus.CREATED);
//    }
//
//    @PostMapping("/api/curriculum-upload")
//    public ResponseEntity<CommonResponse> curriculumFileUpload(@RequestBody MultipartFile file) {
//        fileService.curriculumFileUpload(file);
//        return new ResponseEntity<>(new CommonResponse("파일이 업로드되었습니다."), HttpStatus.CREATED);
//    }
//
//    @PostMapping("/api/graduation-upload")
//    public ResponseEntity<CommonResponse> graduationFileUpload(@RequestBody MultipartFile file) {
//        fileService.graduationFileUpload(file);
//        return new ResponseEntity<>(new CommonResponse("파일이 업로드되었습니다."), HttpStatus.CREATED);
//    }
//
//    @PostMapping("/api/equivalence-upload")
//    public ResponseEntity<CommonResponse> equivalenceFileUpload(@RequestBody MultipartFile file) {
//        fileService.equivalenceFileUpload(file);
//        return new ResponseEntity<>(new CommonResponse("파일이 업로드되었습니다."), HttpStatus.CREATED);
//    }
//
//    @PostMapping("/api/schedule-upload")
//    public ResponseEntity<CommonResponse> scheduleFileUpload(@RequestBody MultipartFile file) {
//        fileService.scheduleFileUpload(file);
//        return new ResponseEntity<>(new CommonResponse("파일이 업로드되었습니다."), HttpStatus.CREATED);
//    }
}
