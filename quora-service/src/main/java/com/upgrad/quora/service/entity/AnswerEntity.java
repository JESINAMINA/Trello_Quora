package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")
@NamedQueries(
        {
                @NamedQuery(name = "answerEntityByUuid", query = "select a from AnswerEntity a where a.uuid = :uuid"),
                @NamedQuery(name = "answersByQuestionId", query = "select a from AnswerEntity a inner join a" +
                        ".question q where q.uuid = :uuid"),
        }
)
public class AnswerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "ans")
    @NotNull
    @Size(max = 255)
    private String answer;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
    public ZonedDateTime getDate() {
        return date;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
    public UserEntity getUser() {
        return user;
    }


    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
    public QuestionEntity getQuestion() {
        return question;
    }

}