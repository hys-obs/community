package com.ys.community.controller;

import com.ys.community.cache.TagCache;
import com.ys.community.dto.QuestionDTO;
import com.ys.community.model.Question;
import com.ys.community.model.User;
import com.ys.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {


    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Integer id,
                       Model model) {
        QuestionDTO question = questionService.findById(id);
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", id);

        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    // 渲染页面
    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    // 用请求方式的不同区别不同的两个方法
    // 发送发布的请求
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Integer id,
            HttpServletRequest request,
            Model model
    ) {
        // 将内容添加到session域中，方便内容的回显
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());

        if (title == null || title == "") {
            model.addAttribute("error", "标题不能为空");
            return "/publish";
        }
        if (description == null || description == "") {
            model.addAttribute("error", "问题描述不能为空");
            return "/publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("error", "标签不能为空");
            return "/publish";
        }

        String invalid = TagCache.filterInvalid(tag);
        if (!invalid.equals("")) {
            model.addAttribute("error", "输入非法字符:" + invalid);
            return "/publish";
        }

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        question.setId(id);
        question.setViewCount(0);
        question.setCommentCount(0);
        question.setLikeCount(0);

        questionService.createOrUpdate(question);

        return "redirect:/";
    }
}
