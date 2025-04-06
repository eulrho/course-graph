package com.course_graph.dto;

import com.course_graph.enums.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ScheduleDTO {
    private String code;
    private String name;
    private int credit;
    private String grade;
    private String type;

    public void setType(Type type) {
        this.type = type.toString();
    }
}
