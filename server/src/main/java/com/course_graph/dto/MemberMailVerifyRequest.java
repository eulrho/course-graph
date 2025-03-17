package com.course_graph.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class MemberMailVerifyRequest {
    private String email;
    private String code;
}
