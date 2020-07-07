package com.upgrad.quora.api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private UserAdminService userAdminService;
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity < UserDeleteResponse > userDelete(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        String bearer = accessToken;
        String uuid = userAdminService.deleteUser(userUuid, bearer);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(uuid).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity < UserDeleteResponse > (userDeleteResponse, HttpStatus.OK);
    }
}