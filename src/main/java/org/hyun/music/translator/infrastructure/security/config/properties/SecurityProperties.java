package org.hyun.music.translator.infrastructure.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="security")
@PropertySource("classpath:security.properties")
public class SecurityProperties {
    private String backendAdminUser;
    private String backendAdminPassword;

    // JWT Token Provider Variables
    private String jwtSecret;
    private int jwtExpirationInMs;

    public String getBackendAdminUser() {
        return backendAdminUser;
    }

    public void setBackendAdminUser(String backendAdminUser) {
        this.backendAdminUser = backendAdminUser;
    }

    public String getBackendAdminPassword() {
        return backendAdminPassword;
    }

    public void setBackendAdminPassword(String backendAdminPassword) {
        this.backendAdminPassword = backendAdminPassword;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public int getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }

    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

}
