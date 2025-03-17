package com.course_graph.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MailRequest {
    @NotBlank
    @Email
    private String email;
}
