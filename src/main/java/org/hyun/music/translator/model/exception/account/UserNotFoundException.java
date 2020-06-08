package org.hyun.music.translator.model.exception.account;

import org.hyun.music.translator.api.account.properties.AccountErrorCodes;
import org.hyun.music.translator.model.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(AccountErrorCodes accountErrorCodes, final String message){
        super(accountErrorCodes.getUserNotFound(), message);
    }
}
