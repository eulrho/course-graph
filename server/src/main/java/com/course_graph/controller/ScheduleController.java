package com.course_graph.controller;

import com.course_graph.dto.*;
import com.course_graph.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/api/schedules")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules(@RequestParam String grade) {
        List<ScheduleDTO> scheduleDTOList = scheduleService.getAllSchedules(grade);
        return new ResponseEntity<>(scheduleDTOList, HttpStatus.OK);
    }

    @GetMapping("/api/schedule")
    public ResponseEntity<List<ScheduleTimeDTO>> getUserSchedules(Authentication auth) {
        List<ScheduleTimeDTO> scheduleDTOList = scheduleService.getUserSchedules(auth.getName());
        return new ResponseEntity<>(scheduleDTOList, HttpStatus.OK);
    }

    @PostMapping("/api/schedule")
    public ResponseEntity<CommonResponse> updateUserSchedules(@RequestBody List<ScheduleUpdateRequest> scheduleUpdateList, Authentication auth) {
        scheduleService.updateSchedules(auth.getName(), scheduleUpdateList);
        return new ResponseEntity<>(new CommonResponse("수정 사항이 반영되었습니다."), HttpStatus.OK);
    }

    @PostMapping("/api/schedule-general")
    public ResponseEntity<CommonResponse> updateUserGeneralSchedules(@RequestBody List<GeneralScheduleUpdateRequest> generalScheduleUpdateList, Authentication auth) {
        scheduleService.updateGeneralSchedules(auth.getName(), generalScheduleUpdateList);
        return new ResponseEntity<>(new CommonResponse("수정 사항이 반영되었습니다."), HttpStatus.OK);
    }
}
