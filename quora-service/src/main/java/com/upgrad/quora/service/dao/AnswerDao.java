package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.AnswerEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

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

}