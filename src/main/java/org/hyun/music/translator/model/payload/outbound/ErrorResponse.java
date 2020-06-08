package org.hyun.music.translator.model.payload.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    @JsonProperty(value="error_info")
    private List<ErrorInfo> errorInfos;

    public ErrorResponse(){}
    public ErrorResponse(String errorCode, String errorMessage){addErrorInfo(errorCode, errorMessage);}

    private class ErrorInfo{
        @JsonProperty("error_code")
        private String errorCode;

        @JsonProperty("error_message")
        private String errorMessage;

        public ErrorInfo(){}
        public ErrorInfo(String errorCode, String errorMessage){
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public void addErrorInfo(String errorCode, String errorMessage){
        if(errorInfos == null){
            errorInfos = new ArrayList<>();
        }
        errorInfos.add(new ErrorInfo(errorCode, errorMessage));
    }
}
