package org.hyun.music.translator.model.account.exception;

import org.springframework.beans.factory.annotation.Value;

public class UserNotFoundException extends Throwable{

    @Value("${account.userNotFound}")
    private String errorCode;
    public UserNotFoundException(final String message){super(message);}

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
