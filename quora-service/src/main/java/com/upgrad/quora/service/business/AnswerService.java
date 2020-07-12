package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    //Delete answer
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String userId, String answerUuid)
            throws AnswerNotFoundException, AuthorizationFailedException {

        AnswerEntity answerEntity = answerDao.getAnswerByUuid(answerUuid);

        if(answerEntity == null){
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        if(!userId.equals(answerEntity.getUser().getUuid()) &&
                !answerEntity.getUser().getRole().equalsIgnoreCase("admin")){
            throw new AuthorizationFailedException("ATHR-003",
                    "Only the answer owner or admin can delete the answer");
        }

        return answerDao.deleteAnswer(answerEntity);

    }

}
