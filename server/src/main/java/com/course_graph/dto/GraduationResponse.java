package com.course_graph.dto;

import com.course_graph.entity.GraduationEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GraduationResponse {
    private int requiredMinCredit;
    private int electiveMinCredit;
    private int totalRequiredCredit;
    private int totalElectiveCredit;
    private List<String> notTakenRequiredSubjects;

    public static GraduationResponse toGraduationResponse(GraduationEntity graduationEntity, int totalRequiredCredit, int totalElectiveCredit, List<String> notTakenRequiredSubjects) {
        GraduationResponse graduationResponse = new GraduationResponse();
        graduationResponse.setRequiredMinCredit(graduationEntity.getRequiredMinCredit());
        graduationResponse.setElectiveMinCredit(graduationEntity.getElectiveMinCredit());
        graduationResponse.setTotalRequiredCredit(totalRequiredCredit);
        graduationResponse.setTotalElectiveCredit(totalElectiveCredit);
        graduationResponse.setNotTakenRequiredSubjects(notTakenRequiredSubjects);
        return graduationResponse;
    }
}
