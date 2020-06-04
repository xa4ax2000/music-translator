package org.hyun.music.translator.api.account;

import org.hyun.music.translator.api.ApiController;
import org.hyun.music.translator.model.account.exception.UserExistsException;
import org.hyun.music.translator.model.account.exception.UserNotFoundException;
import org.hyun.music.translator.model.auth.Authority;
import org.hyun.music.translator.model.payload.inbound.DeleteUserRequest;
import org.hyun.music.translator.model.payload.inbound.SignUpRequest;
import org.hyun.music.translator.model.payload.outbound.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('"+ Authority.ROLE_SUPER_USER+"')")
public class AuthController extends ApiController {

    Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserService userService;

    @RequestMapping(value=REGISTER_USER_URL, consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody SignUpRequest signUpRequest){
        try {
            userService.registerNewUserAccount(signUpRequest);
            return ResponseEntity.ok().build();
        }catch(UserExistsException ex){
            return ResponseEntity.ok(new ErrorResponse(ex.getErrorCode(), ex.getMessage()));
        }catch(Exception ex){
            LOGGER.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value=DELETE_USER_URL, consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> delete(@RequestBody DeleteUserRequest deleteUserRequest){
        try{
            userService.deleteUserAccount(deleteUserRequest);
            return ResponseEntity.ok().build();
        }catch(UserNotFoundException ex){
            return ResponseEntity.ok(new ErrorResponse(ex.getErrorCode(), ex.getMessage()));
        }catch(Exception ex){
            LOGGER.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}