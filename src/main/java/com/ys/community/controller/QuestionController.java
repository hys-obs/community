package com.ys.community.controller;

import com.ys.community.dto.CommentDTO;
import com.ys.community.dto.QuestionDTO;
import com.ys.community.enums.CommentTypeEnum;
import com.ys.community.service.CommentService;
import com.ys.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model) {
        QuestionDTO questionDto = questionService.findById(id);
        List<QuestionDTO> relatedQuestions = questionService.findRelated(questionDto);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        // 统计阅读数
        questionService.incrView(id);

        model.addAttribute("question", questionDto);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
