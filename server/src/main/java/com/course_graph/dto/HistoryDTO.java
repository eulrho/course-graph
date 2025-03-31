package com.course_graph.dto;

import com.course_graph.entity.HistoryEntity;
import com.course_graph.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class HistoryDTO {
    private String subjectName;
    private String score;

    public static HistoryDTO toHistoryDTO(HistoryEntity historyEntity) {
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setSubjectName(historyEntity.getSubjectEntity().getName());
        historyDTO.setScore(historyEntity.getScore());
        return historyDTO;
    }
}
