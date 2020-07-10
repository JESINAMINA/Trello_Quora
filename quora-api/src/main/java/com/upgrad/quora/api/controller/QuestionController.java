package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    QuestionService questionService;

    //Create question
    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> postQuestion(
            @RequestHeader("authorization") final String authorization,
            final QuestionRequest questionRequest)
            throws AuthorizationFailedException, AuthenticationFailedException, InvalidQuestionException {

        //Get bearer access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateBearerAuthorization(accessToken);

        //Get user details
        UserEntity user = userAuthTokenEntity.getUser();

        //Update question's content
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser(user);
        questionService.createQuestion(questionEntity);

        QuestionResponse questionResponse = new QuestionResponse()
                .id(questionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);

    }

    //Get all questions
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AuthenticationFailedException {

        //Get bearer access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateBearerAuthorization(accessToken);

        //Get user details
        UserEntity user = userAuthTokenEntity.getUser();

        //Get all questions and send across ResponseEntity
        List<QuestionEntity> questionEntityList = questionService.getAllQuestions();

        return getListResponseEntity(questionEntityList);


    }

    private ResponseEntity<List<QuestionDetailsResponse>> getListResponseEntity(List<QuestionEntity> questionEntityList) {
        List<QuestionDetailsResponse> res = new ArrayList<QuestionDetailsResponse>();

        for (QuestionEntity q : questionEntityList){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.id(q.getUuid());
            questionDetailsResponse.content(q.getContent());
            res.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(res, HttpStatus.OK);
    }


    //Edit question
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionId, final QuestionEditRequest questionEditRequest)
            throws AuthenticationFailedException, AuthorizationFailedException, InvalidQuestionException {

        //Get bearer access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateBearerAuthorization(accessToken);

        //Get user details
        UserEntity user = userAuthTokenEntity.getUser();

        //Edit question
        QuestionEntity questionEntity = questionService.
                editQuestion(questionEditRequest.getContent(), user.getUuid(), questionId);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse()
                .id(questionEntity.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);

    }

    //Delete question
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionId)
            throws AuthorizationFailedException, AuthenticationFailedException, InvalidQuestionException {

        //Get bearer access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateBearerAuthorization(accessToken);

        //Get user details
        UserEntity user = userAuthTokenEntity.getUser();

        //Delete question
        QuestionEntity questionEntity = questionService.deleteQuestion(user, questionId);
        QuestionDeleteResponse deleteResponse = new QuestionDeleteResponse().
                id(questionEntity.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(deleteResponse,HttpStatus.OK);

    }

    //Get all questions posted by specific user
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("userId") final String userId)
            throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {

        //Get bearer access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateBearerAuthorization(accessToken);

        //Get user details
        UserEntity user = userAuthTokenEntity.getUser();

        //Get all questions and send across ResponseEntity
        List<QuestionEntity> questionEntityList = questionService.getAllQuestionsByUser(userId);

        return getListResponseEntity(questionEntityList);

    }

}
