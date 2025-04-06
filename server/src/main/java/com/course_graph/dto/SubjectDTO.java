package com.course_graph.dto;

import com.course_graph.entity.SubjectEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectDTO {
    private String name;
    private String grade;

    public static SubjectDTO toSubjectDTO(SubjectEntity subjectEntity) {
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setName(subjectEntity.getName());
        subjectDTO.setGrade(subjectEntity.getGrade());
        return subjectDTO;
    }
}
