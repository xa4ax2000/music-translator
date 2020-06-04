package org.hyun.music.translator.model.account.exception;

import org.springframework.beans.factory.annotation.Value;

public class UserExistsException extends Throwable{

    @Value("${account.userExists}")
    private String errorCode;
    public UserExistsException(final String message){super(message);}

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
