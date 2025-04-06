package com.course_graph.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryPageResponse <T> {
    private T data;
    private PageInfoDTO pageInfo;
}
