package com.course_graph.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfoDTO {
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;

    public static PageInfoDTO toPageInfoDTO(int totalElements, int totalPages, int page) {
        return new PageInfoDTO(totalElements, totalPages, page, 12);
    }
}
