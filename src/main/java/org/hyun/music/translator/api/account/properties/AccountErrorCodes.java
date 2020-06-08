package org.hyun.music.translator.api.account.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="account")
@PropertySource("classpath:error_codes.properties")
public class AccountErrorCodes {
    private String userExists;
    private String userNotFound;

    public String getUserExists() {
        return userExists;
    }

    public void setUserExists(String userExists) {
        this.userExists = userExists;
    }

    public String getUserNotFound() {
        return userNotFound;
    }

    public void setUserNotFound(String userNotFound) {
        this.userNotFound = userNotFound;
    }
}
