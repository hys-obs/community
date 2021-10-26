package com.ys.community.controller;

import com.ys.community.dto.PaginationDTO;
import com.ys.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 首页
 */
@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    /**
     * 首页
     * @param model 用于将获取到的数据传入页面展示
     * @param page 当前页数，默认第一页
     * @param size 每页的问题数
     * @return
     */
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search) {
        PaginationDTO pagination = questionService.list(search, page, size);
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);


        return "index";
    }
}
