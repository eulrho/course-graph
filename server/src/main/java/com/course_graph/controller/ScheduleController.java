package com.course_graph.controller;

import com.course_graph.dto.CommonResponse;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/api/schedules")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOList = scheduleService.getAllSchedules();
        return new ResponseEntity<>(scheduleDTOList, HttpStatus.OK);
    }
}
