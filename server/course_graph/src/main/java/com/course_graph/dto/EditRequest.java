package com.course_graph.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EditRequest {
    private String email;
    private String newPassword;
    private String newPasswordCheck;
    private int year;
}
