package com.course_graph.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class fileRequest {
    private String fileName;
    private String newPassword;
    private String newPasswordCheck;
}
