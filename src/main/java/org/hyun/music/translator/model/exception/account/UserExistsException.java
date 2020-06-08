package org.hyun.music.translator.model.exception.account;

import org.hyun.music.translator.api.account.properties.AccountErrorCodes;
import org.hyun.music.translator.model.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;

public class UserExistsException extends BaseException {
    public UserExistsException(AccountErrorCodes accountErrorCodes, final String message){
        super(accountErrorCodes.getUserExists(), message);
    }
}
