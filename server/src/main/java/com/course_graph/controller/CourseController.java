package com.course_graph.controller;

import com.course_graph.dto.HistoryDTO;
import com.course_graph.dto.CourseStatusDTO;
import com.course_graph.dto.HistoryPageResponse;
import com.course_graph.dto.PageInfoDTO;
import com.course_graph.service.CourseService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

//    @PatchMapping("/api/history")
//    public ResponseEntity<Void> updateHistory(Authentication auth) {
//        courseService.updateHistory(auth.getName());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
