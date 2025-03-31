package com.course_graph.service;

import com.course_graph.dto.HistoryDTO;
import com.course_graph.entity.HistoryEntity;
import com.course_graph.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final UserService userService;

    public List<HistoryDTO> getUserHistory(String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        List<HistoryEntity> historyEntityList = userEntity.getHistoryEntityList();

        List<HistoryDTO> historyDTOList = new ArrayList<>();
        for (HistoryEntity data : historyEntityList) {
            HistoryDTO historyDTO = HistoryDTO.toHistoryDTO(data);
            historyDTOList.add(historyDTO);
        }
        return historyDTOList;
    }
}