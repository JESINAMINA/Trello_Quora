package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
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

        //Get access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService
                .validateBearerAuthorization(accessToken);

        //Get user details and create question
        UserEntity user = userAuthTokenEntity.getUser();
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

}
