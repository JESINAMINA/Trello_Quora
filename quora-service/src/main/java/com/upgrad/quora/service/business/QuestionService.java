package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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

    //Edit question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(String content, String userUuid, String questionUuid)
            throws InvalidQuestionException, AuthorizationFailedException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);

        //If question's UUID does not exist
        if (questionEntity == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        //If current user is not the question owner
        if (userUuid != null && !userUuid.equals(questionEntity.getUser().getUuid())){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        }

        //If the new content is null or empty or equal to existing content
        if (content == null || content.isEmpty() || content.trim().isEmpty() ||
                content.equalsIgnoreCase(questionEntity.getContent())){
            throw new InvalidQuestionException("QUES-004",
                    "New content cannot be empty or null or equal to existing content");
        }

        questionEntity.setContent(content);
        questionDao.updateQuestion(questionEntity);
        return questionDao.getQuestionByUuid(questionUuid);

    }

    //Delete question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(UserEntity user, String questionUuid)
            throws InvalidQuestionException, AuthorizationFailedException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);

        //If question's UUID does not exist
        if (questionEntity == null){
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        //If the logged in user is not the question owner or non-admin
        if (!user.getUuid().equals(questionEntity.getUser().getUuid()) &&
                !user.getRole().equalsIgnoreCase("admin")){
            throw new AuthorizationFailedException("ATHR-003",
                    "Only the question owner or admin can delete the question");
        }

        return questionDao.deleteQuestion(questionEntity);
    }

    //Get all questions posted by a specific user
    public List<QuestionEntity> getAllQuestionsByUser(String uuid) throws UserNotFoundException {

        UserEntity user = userDao.getUserByUuid(uuid);

        if(user == null){
            throw new UserNotFoundException("USR-001",
                    "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionByUser(user);

    }

}
