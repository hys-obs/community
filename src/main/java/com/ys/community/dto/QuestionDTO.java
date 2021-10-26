package com.ys.community.dto;


import com.ys.community.model.User;
import lombok.Data;

/**
 * Question + User
 * Question中没用用户的头像信息，所以创建此类解决
 */
@Data
public class QuestionDTO {
    private Integer id;
    private String title; //标题
    private String description; // 问题描述
    private String tag; // 标签
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator; // 发布者
    private Integer commentCount; // 评论数量
    private Integer viewCount; // 浏览量
    private Integer likeCount; // 点赞数
    private User user;
}
