package com.ys.community.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Github中的用户信息
 */
@Data
@ToString
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
}
