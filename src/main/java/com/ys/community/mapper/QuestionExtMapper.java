package com.ys.community.mapper;

import com.ys.community.dto.QuestionDTO;
import com.ys.community.dto.QuestionQueryDTO;
import com.ys.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int incrView(Question record);
    int incrCommentCount(Question record);
    List<Question> selectRegexp(Question questionDTO);

    int countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}