package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    private UserAuthenticationService userAuthenticationService ;
    //Get bearer access token
    public String getBearerAccessToken(final String authorization) throws AuthenticationFailedException {

        String accessToken = null;
        String[] tokens = authorization.split("Bearer ");

        try {
            accessToken = tokens[1];
        } catch (IndexOutOfBoundsException ie){
            accessToken = tokens[0];
            if (accessToken == null){
                throw new AuthenticationFailedException("ATHR-004","Format should be: 'Bearer accessToken'");
            }
        }

        return accessToken;
    }

    //Validate bearer authorization token
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity validateBearerAuthorization(final String accessToken)
            throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthenticationToken(accessToken);

        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if(userAuthenticationService.isSignedOut(accessToken)){
            throw new AuthorizationFailedException
                    ("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        return userAuthTokenEntity;
    }

}
