package com.upgrad.quora.api.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.upgrad.quora.service.exception.*;
import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserCommonService;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserCommonService userCommonssService;

   // get the user profile using the user uuid and accesstoken as input

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity < UserDetailsResponse > userProfile(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        String bearer = accessToken;
        final UserEntity userEntity = userCommonssService.getUser(userUuid, bearer);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).userName(userEntity.getUserName()).emailAddress(userEntity.getEmail())
                .country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe()).dob(userEntity.getDob()).contactNumber(userEntity.getContactNumber());
        return new ResponseEntity < UserDetailsResponse > (userDetailsResponse, HttpStatus.OK);
    }

}