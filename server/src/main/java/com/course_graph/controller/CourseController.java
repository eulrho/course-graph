package com.course_graph.controller;

import com.course_graph.dto.HistoryDTO;
import com.course_graph.dto.CourseStatusDTO;
import com.course_graph.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/api/history")
    public ResponseEntity<List<HistoryDTO>> history(Authentication auth) {
        List<HistoryDTO> historyDTOList = courseService.getUserHistory(auth.getName());
        return new ResponseEntity<>(historyDTOList, HttpStatus.OK);
    }

    @GetMapping("/api/course")
    public ResponseEntity<List<CourseStatusDTO>> course(Authentication auth) {
        List<CourseStatusDTO> courseStatusDTOList = courseService.getUserCourse(auth.getName());
        return new ResponseEntity<>(courseStatusDTOList, HttpStatus.OK);
    }
}
