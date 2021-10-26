package com.ys.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private String search;
    private int page;
    private int size;
}
