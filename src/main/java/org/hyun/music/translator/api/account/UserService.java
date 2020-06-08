package org.hyun.music.translator.api.account;

import org.hyun.music.translator.model.exception.account.UserExistsException;
import org.hyun.music.translator.model.exception.account.UserNotFoundException;
import org.hyun.music.translator.model.payload.inbound.DeleteUserRequest;
import org.hyun.music.translator.model.payload.inbound.SignUpRequest;

public interface UserService {

    void registerNewUserAccount(SignUpRequest signUpRequest) throws UserExistsException;

    void deleteUserAccount(DeleteUserRequest deleteUserRequest) throws UserNotFoundException;
}