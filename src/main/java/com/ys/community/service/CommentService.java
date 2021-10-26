package com.ys.community.service;

import com.ys.community.dto.CommentDTO;
import com.ys.community.enums.CommentTypeEnum;
import com.ys.community.enums.NotificationTypeEnum;
import com.ys.community.enums.NotificationStatusEnum;
import com.ys.community.exception.CustomizeException;
import com.ys.community.exception.CustomizeExceptionCode;
import com.ys.community.mapper.*;
import com.ys.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeExceptionCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeExceptionCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            // 回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeExceptionCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);

            Notification notification = new Notification();
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setType(NotificationTypeEnum.REPLY_COMMENT.getType());
            notification.setOuterid(comment.getParentId());
            notification.setOuterTitle(comment.getContent());
            notification.setNotifier(comment.getCommentator());
            notification.setNotifierName(commentator.getName());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notification.setReceiver(dbComment.getCommentator());
            notificationMapper.insert(notification);

        } else {
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeExceptionCode.QUESTION_NOT_FOUND);
            }
            // 增加评论数

            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incrCommentCount(question);


            // 自己评论不通知
            if (question.getCreator() == comment.getCommentator()) {
                return;
            }
            // 创建通知
            Notification notification = new Notification();
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setType(NotificationTypeEnum.REPLY_QUESTION.getType());
            notification.setOuterid(question.getId());
            notification.setOuterTitle(question.getTitle());
            notification.setNotifier(comment.getCommentator());
            notification.setNotifierName(commentator.getName());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notification.setReceiver(question.getCreator());
            notificationMapper.insert(notification);
        }
    }

    public List<CommentDTO> listByTargetId(Integer id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        Set<Integer> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Integer> userIds = new ArrayList<>();
        userIds.addAll(commentators);
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
