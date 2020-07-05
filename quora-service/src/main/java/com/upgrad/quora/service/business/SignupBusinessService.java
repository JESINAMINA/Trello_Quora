package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if (!isUserExists(userEntity) && !isUserEmailIdExists(userEntity)) {
            String[] encrytedPassword = passwordCryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encrytedPassword[0]);
            userEntity.setPassword(encrytedPassword[1]);
            return userDao.createUser(userEntity);
        }
        return null;

    }
    private boolean isUserExists(UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity entity = userDao.getUserByUserName(userEntity.getUserName());
        if (entity != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else {
            return false;
        }
    }
    private boolean isUserEmailIdExists(UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity emailEntity = userDao.getUserByEmail(userEntity.getEmail());
        if (emailEntity != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        } else {
            return false;
        }
    }
}