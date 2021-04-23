package com.ys.community.mapper;

import com.ys.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表的相关操作
 */
@Mapper
public interface UserMapper {

    // 将github上的用户信息持久化到数据库
    @Insert("insert into user(account_id,name,token,gmt_creat,gmt_modified) values(#{accountId},#{name},#{token},#{gmtCreat},#{gmtModified})")
    public void insert(User user);

    // 根据cookie(token)查找用户，确认登录的情况
    @Select("select * from user where token = #{token}")
    User findByToken(String token);
}
