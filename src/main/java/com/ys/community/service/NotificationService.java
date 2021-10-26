package com.ys.community.service;


import com.ys.community.dto.NotificationDTO;
import com.ys.community.dto.PaginationDTO;
import com.ys.community.enums.NotificationTypeEnum;
import com.ys.community.enums.NotificationStatusEnum;
import com.ys.community.exception.CustomizeException;
import com.ys.community.exception.CustomizeExceptionCode;
import com.ys.community.mapper.NotificationMapper;
import com.ys.community.model.*;
import org.apache.ibatis.session.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    public PaginationDTO list(Integer id, Integer page, Integer size) {

        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(id);
        Integer totalCount = (int) notificationMapper.countByExample(example);
        // 计算总页数
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        if (page < 0) {
            page = 1;
        }

        List<NotificationDTO> notificationDTOs = new ArrayList<>();
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO();
        paginationDTO.setData(notificationDTOs);
        paginationDTO.setPagination(totalPage, page);

        //计算offset和size
        Integer offset = size * (page - 1);

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(id);
        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));

        if (notifications.size() == 0) {
            return paginationDTO;
        }

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTOs.add(notificationDTO);
        }

        ArrayList<Object> objects = new ArrayList<>();
        return paginationDTO;
    }

    public Long notificationCount(Integer id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Integer id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
             throw new CustomizeException(CustomizeExceptionCode.NOTIFICATION_NOT_FOUND);
        }
        if (user.getId() != notification.getReceiver()) {
             throw new CustomizeException(CustomizeExceptionCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

        return notificationDTO;
    }
}
