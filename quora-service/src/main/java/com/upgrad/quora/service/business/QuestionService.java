package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    //Create question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) throws InvalidQuestionException {

        String content = questionEntity.getContent();
        if(content == null || content.isEmpty() || content.trim().isEmpty()){
            throw new InvalidQuestionException("QUES-002","Question's content cannot be empty");
        }

        if (questionDao.getQuestionByContent(content.trim()) != null){
            throw new InvalidQuestionException("QUES-003", "Question already exists");
        }

        return questionDao.createQuestion(questionEntity);

    }

    //Get all questions
    public List<QuestionEntity> getAllQuestions(){

        return questionDao.getAllQuestions();

    }

}
