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

    @PatchMapping("/api/history")
    public ResponseEntity<CommonResponse> updateHistory(@RequestBody List<ScoreUpdateRequest> updateRequests, Authentication auth) {
        courseService.updateHistory(updateRequests, auth.getName());
        return new ResponseEntity<>(new CommonResponse("수정 사항이 반영되었습니다."),HttpStatus.OK);
    }
}
