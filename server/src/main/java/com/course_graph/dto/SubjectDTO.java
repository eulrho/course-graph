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
public class SubjectDTO {
    private String code;
    private String name;
    private int credit;
    private String semester;
    private String grade;
    private String type;
    private int createdAt;
    private int deletedAt;

    public void setType(Type type) {
        this.type = type.toString();
    }
}
