package org.hyun.music.translator.model.payload.inbound;

import javax.validation.constraints.NotEmpty;

public class DeleteUserRequest {

    @NotEmpty
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}