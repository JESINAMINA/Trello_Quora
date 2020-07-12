package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.CreateAnswerBusinessService;
import com.upgrad.quora.service.business.EditAnswerBusinessService;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    EditAnswerBusinessService editAnswerBusinessService;
    @Autowired
    CreateAnswerBusinessService createAnswerBusinessService;
    @Autowired
    QuestionDao questionDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AnswerService answerService;

    @PostMapping(path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException
    {
        String accessToken = null;
        String[] tokens = authorization.split("Bearer ");
        try {
            accessToken = tokens[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            accessToken = tokens[0];
        }


        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());

        // Return response with the answer entity
        final AnswerEntity createdAnswerEntity =
                createAnswerBusinessService.createAnswer(answerEntity, questionId,accessToken);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest,
                                                                @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException
    {

        // Syntax : token or Bearer <accesstoken>.
        String accessToken = null;
        String[] tokens = authorization.split("Bearer ");
        try {
            accessToken = tokens[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            accessToken = tokens[0];
        }

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerEditRequest.getContent());
        answerEntity.setUuid(answerId);

        // Return response with new answer entity
        AnswerEntity updatedAnswerEntity = editAnswerBusinessService.editAnswerContent(answerEntity,accessToken);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //Delete answer
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("answerId") final String answerId)
            throws AuthenticationFailedException, AuthorizationFailedException, AnswerNotFoundException {

        //Get bearer access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateBearerAuthorization(accessToken);

        //Get user details
        UserEntity user = userAuthTokenEntity.getUser();

        //Delete answer
        AnswerEntity answerEntity = answerService.deleteAnswer(user.getUuid(),answerId);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().
                id(answerEntity.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);

    }

    //Get all answers for specific/given question
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersForGivenQuestion(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionId)
            throws AuthenticationFailedException, AuthorizationFailedException, InvalidQuestionException {

        //Get bearer access token
        String accessToken = authenticationService.getBearerAccessToken(authorization);

        //Bearer authentication
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateBearerAuthorization(accessToken);

        //Get question id
        QuestionEntity questionEntity = answerService.getQuestionById(questionId);

        //Return response entity with all the answers for the given question
        List<AnswerEntity> answerEntityList = answerService.getAllAnswersForGivenQuestion(questionEntity);
        List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<>();

        for (AnswerEntity answerEntity : answerEntityList){
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
            answerDetailsResponse.setQuestionContent(answerEntity.getQuestion().getContent());
            answerDetailsResponseList.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList,HttpStatus.OK);

    }

}