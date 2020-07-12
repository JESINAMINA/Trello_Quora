package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }
    public AnswerEntity editAnswerContent(final AnswerEntity answer) {
        return entityManager.merge(answer);
    }

    public AnswerEntity getAnswerByUuid(String questionId) {
        try {
            return entityManager.createNamedQuery("answerEntityByUuid", AnswerEntity.class).setParameter("uuid", questionId).getSingleResult();

        } catch (NoResultException e) {

            return null;
        }
    }

    //Delete answer
    @OnDelete(action = OnDeleteAction.CASCADE)
    public AnswerEntity deleteAnswer(final AnswerEntity answerEntity){

        entityManager.remove(answerEntity);
        return answerEntity;

    }

    ////Get all answers for specific/given question
    public List<AnswerEntity> getAllAnswersForGivenQuestion(final QuestionEntity questionEntity){

        return entityManager.createNamedQuery("answersByQuestionEntity", AnswerEntity.class)
                .setParameter("questionEntity", questionEntity).getResultList();

    }

}