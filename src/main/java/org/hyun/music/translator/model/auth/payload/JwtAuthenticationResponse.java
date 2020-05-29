package org.hyun.music.translator.model.auth.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude()
public class JwtAuthenticationResponse {
    @JsonProperty(value="access_token")
    private String accessToken;

    public JwtAuthenticationResponse(String accessToken){this.accessToken = accessToken;}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
