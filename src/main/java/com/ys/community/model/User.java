package com.ys.community.model;

import lombok.Data;
import lombok.ToString;

/**
 * 数据库中的用户信息
 */
@Data
@ToString
public class User {
    private Integer id;
    private String accountId;
    private String name;
    private String token;
    private Long gmtCreat;
    private Long gmtModified;
}
