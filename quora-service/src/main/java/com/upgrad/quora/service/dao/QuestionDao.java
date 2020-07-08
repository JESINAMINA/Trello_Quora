package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Create question
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    //Get question by content
    public QuestionEntity getQuestionByContent(final String content){
        try {
            return entityManager.createNamedQuery("QuestionEntityByContent", QuestionEntity.class)
                    .setParameter("content",content).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

}
