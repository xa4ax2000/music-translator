package org.hyun.music.translator.model.exception;

public abstract class BaseException extends Throwable {

    private String errorCode;

    public BaseException(final String errorCode, final String message){
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
