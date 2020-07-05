package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthenticationService userAuthenticationService ;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userUuid, final String accessToken) throws
            AuthorizationFailedException, UserNotFoundException
    {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthenticationToken(accessToken);

        //Check if the user has signed-in
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        //Check if the user is signed-out
        if(userAuthenticationService.isSignedOut(accessToken))
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }

        //Check whether the user is an admin
        String userRole = userAuthTokenEntity.getUser().getRole();
        if(userRole.equals("nonadmin"))
        {
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }
        UserEntity user = userDao.getUserByUuid(userUuid);

        //Check if the user exists
        if(user == null)
        {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        } else {
            userDao.deleteUser(userUuid);
            return userUuid;
        }
    }
}