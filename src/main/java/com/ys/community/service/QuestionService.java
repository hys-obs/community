package com.ys.community.service;

import com.ys.community.dto.PaginationDTO;
import com.ys.community.dto.QuestionDTO;
import com.ys.community.dto.QuestionQueryDTO;
import com.ys.community.exception.CustomizeException;
import com.ys.community.exception.CustomizeExceptionCode;
import com.ys.community.mapper.QuestionExtMapper;
import com.ys.community.mapper.QuestionMapper;
import com.ys.community.mapper.UserMapper;
import com.ys.community.model.Question;
import com.ys.community.model.QuestionExample;
import com.ys.community.model.User;
import com.ys.community.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;


    public PaginationDTO list(String search, Integer page, Integer size) {

        if (search != null) {
            String[] tags = search.split(" ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
//        Integer totalCount = questionMapper.count();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        int totalCount = questionExtMapper.countBySearch(questionQueryDTO);
//        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        // offset偏移量
//        Integer offset = 0;
//        if (page == 0) {
//            offset = size * page;
//        } else {
//            offset = size * (page - 1);
//        }

//        List<Question> questions = questionMapper.list(offset, size);

        Integer offset = size * (page - 1);

        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            // 根据creator属性查出用户对象，再查出用户的头像信息，封装到QuestionDTO中
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andIdEqualTo(question.getCreator());
            List<User> users = userMapper.selectByExample(userExample);
//            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(users.get(0));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;
//        Integer totalCount = questionMapper.countByUserId(userId);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        questionExample.setOrderByClause("gmt_create desc");
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }

        if (page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        // offset偏移量
        Integer offset = size * (page - 1);
//        List<Question> questionList = questionMapper.listByUserId(userId, offset, size);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            // 根据creator属性查出用户对象，再查出用户的头像信息，封装到QuestionDTO中
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andIdEqualTo(question.getCreator());
            List<User> users = userMapper.selectByExample(userExample);
//            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(users.get(0));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }

    public QuestionDTO findById(Integer id) {
//        Question question = questionMapper.findById(id);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andIdEqualTo(id);
        List<Question> questions = questionMapper.selectByExample(questionExample);
        if (questions.size() == 0) {
            throw new CustomizeException(CustomizeExceptionCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(questions.get(0), questionDTO);
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdEqualTo(questions.get(0).getCreator());
        List<User> users = userMapper.selectByExample(userExample);
//        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(users.get(0));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            // 插入
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        } else {
            // 更新
//            question.setGmtModified(System.currentTimeMillis());
//            questionMapper.update(question);
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1) {
                throw new CustomizeException(CustomizeExceptionCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incrView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incrView(question);
    }

    public List<QuestionDTO> findRelated(QuestionDTO questionDto) {
        if (questionDto.getTag() == null) {
            return new ArrayList<>();
        }

        String tag = questionDto.getTag().replaceAll(",", "|");

        Question question = new Question();
        question.setId(questionDto.getId());
        question.setTag(tag);

        List<Question> questions = questionExtMapper.selectRegexp(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
