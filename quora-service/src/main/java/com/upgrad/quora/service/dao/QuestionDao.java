package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    //Get all questions
    public List<QuestionEntity> getAllQuestions(){

        return entityManager.createQuery("SELECT q from QuestionEntity q",QuestionEntity.class)
                .getResultList();

    }

    //Get question by UUID
    public QuestionEntity getQuestionByUuid(final String questionUuid){

        try {
            return entityManager.createNamedQuery("QuestionEntityByUuid", QuestionEntity.class)
                    .setParameter("uuid",questionUuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }

    }

    //Update question
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity){

        return entityManager.merge(questionEntity);

    }

    //Delete question
    @OnDelete(action = OnDeleteAction.CASCADE)
    public QuestionEntity deleteQuestion(final QuestionEntity questionEntity){

        entityManager.remove(questionEntity);
        return questionEntity;

    }

    //Get all questions posted by specific user
    public List<QuestionEntity> getAllQuestionByUser(UserEntity user){

        return entityManager.createNamedQuery("QuestionEntitiesByUser", QuestionEntity.class)
                .setParameter("user", user).getResultList();

    }

}
