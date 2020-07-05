package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;



@Service
public class SignoutBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signOutService(String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = null;
        //Validate if user is signed in or not
        userAuthTokenEntity = userDao.getUserAuthenticationToken(accessToken);
        if (userAuthTokenEntity != null) {
            final ZonedDateTime now = ZonedDateTime.now();
            userAuthTokenEntity.setLogoutAt(now);
            userAuthTokenEntity = userDao.updateUserLogOut(userAuthTokenEntity);
        } else {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        return userAuthTokenEntity;
    }
}