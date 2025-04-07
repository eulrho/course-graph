package com.course_graph.controller;

import com.course_graph.dto.*;
import com.course_graph.service.CourseService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/api/history")
    public ResponseEntity<HistoryPageResponse> history(@Positive @RequestParam int page, Authentication auth) {
        Page<HistoryDTO> historyPage = courseService.getUserHistory(auth.getName(), page);
        PageInfoDTO pageInfo = PageInfoDTO.toPageInfoDTO((int)historyPage.getTotalElements(), historyPage.getTotalPages(), page);
        return new ResponseEntity<>(new HistoryPageResponse(historyPage.getContent(), pageInfo), HttpStatus.OK);
    }

    @GetMapping("/api/course")
    public ResponseEntity<List<CourseStatusDTO>> course(Authentication auth) {
        List<CourseStatusDTO> courseStatusDTOList = courseService.getUserCourse(auth.getName());
        return new ResponseEntity<>(courseStatusDTOList, HttpStatus.OK);
    }

    @GetMapping("/api/subjects")
    public ResponseEntity<List<CourseStatusDTO>> getAllSubjects(@RequestParam String grade, Authentication auth) {
        List<CourseStatusDTO> courseStatusDTOList = courseService.getUserAllSubjects(grade, auth.getName());
        return new ResponseEntity<>(courseStatusDTOList, HttpStatus.OK);
    }

    @PatchMapping("/api/subjects")
    public ResponseEntity<CommonResponse> updateTakenSubjectList(@RequestBody List<StatusUpdateRequest> updateRequests, Authentication auth) {
        courseService.updateUserTakenSubjectList(updateRequests, auth.getName());
        return new ResponseEntity<>(new CommonResponse("수정 사항이 반영되었습니다."), HttpStatus.OK);
    }

    @PatchMapping("/api/history")
    public ResponseEntity<CommonResponse> updateScores(@RequestBody List<ScoreUpdateRequest> updateRequests, Authentication auth) {
        courseService.updateUserScores(updateRequests, auth.getName());
        return new ResponseEntity<>(new CommonResponse("수정 사항이 반영되었습니다."),HttpStatus.OK);
    }

    @GetMapping("/api/graduation")
    public ResponseEntity<GraduationResponse> getGraduationInfo(Authentication auth) {
        GraduationResponse graduationResponse = courseService.getUserGraduationInfo(auth.getName());
        return new ResponseEntity<>(graduationResponse, HttpStatus.OK);
    }
}
