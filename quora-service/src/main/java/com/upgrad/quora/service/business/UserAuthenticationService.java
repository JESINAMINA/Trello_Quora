package com.upgrad.quora.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;

import java.time.ZonedDateTime;

@Service
public class UserAuthenticationService {


    @Autowired
    UserDao userDao;

    boolean isSignedOut(String accessToken) {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthenticationToken(accessToken);
        ZonedDateTime isLoggedIn = userAuthTokenEntity.getLoginAt();
        ZonedDateTime isLoggedOut = userAuthTokenEntity.getLogoutAt();
        //check if the access token is expired or invalid
        if (isLoggedOut != null && isLoggedOut.isAfter(isLoggedIn)) {
            return true;
        } else return false;
    }

}