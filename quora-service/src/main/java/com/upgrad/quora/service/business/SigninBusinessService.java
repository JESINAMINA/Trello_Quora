package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SigninBusinessService {
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;
    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = null;
        UserEntity userEntity = userDao.getUserByUserName(username);
        //check for userName
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        //check for password
        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());

        //send username and encrypted password to userDao
        userEntity = userDao.authenticateUser(username, encryptedPassword);
        //check if userName and password matches and generate authentication token
        if (userEntity != null) {
            String uuid = userEntity.getUuid();
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userAuthTokenEntity.setUuid(userEntity.getUuid());
            userDao.createAuthToken(userAuthTokenEntity);
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
        return userAuthTokenEntity;
    }
}