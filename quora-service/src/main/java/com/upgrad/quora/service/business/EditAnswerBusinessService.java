package com.upgrad.quora.service.business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;


@Service
public class EditAnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthenticationToken(authorization);

        // Check if the user is signed in
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if the user is signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        // check if the requested answer exists
        AnswerEntity currentAnswer = answerDao.getAnswerByUuid(answerEntity.getUuid());
        if (currentAnswer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Check if the user is the owner of requested answer
        UserEntity user = userAuthEntity.getUser();
        UserEntity ownerUser = answerDao.getAnswerByUuid(answerEntity.getUuid()).getUser();
        if (user.getId() != ownerUser.getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerEntity.setId(currentAnswer.getId());
        answerEntity.setDate(currentAnswer.getDate());
        answerEntity.setUser(currentAnswer.getUser());
        answerEntity.setQuestion(currentAnswer.getQuestion());
        return answerDao.editAnswerContent(answerEntity);
    }
}