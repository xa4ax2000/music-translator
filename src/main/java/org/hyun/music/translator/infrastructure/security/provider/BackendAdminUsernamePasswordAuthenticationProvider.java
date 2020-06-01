package org.hyun.music.translator.infrastructure.security.provider;

import org.hyun.music.translator.infrastructure.security.config.properties.SecurityProperties;
import org.hyun.music.translator.model.auth.Authority;
import org.hyun.music.translator.model.auth.BackendAdminUsernamePasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Optional;

public class BackendAdminUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private static final String INVALID_BACKEND_ADMIN_CREDENTIALS = "Invalid Backend Admin Credentials";

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = Optional
                .ofNullable(authentication.getPrincipal())
                .map(e -> (String)e)
                .orElseThrow(() -> new BadCredentialsException("Username is missing"));
        String password = Optional
                .ofNullable(authentication.getCredentials())
                .map(e -> (String)e)
                .orElseThrow(() -> new BadCredentialsException("Password is missing"));

        if(credentialsInvalid(username, password)){
            throw new BadCredentialsException(INVALID_BACKEND_ADMIN_CREDENTIALS);
        }

        return new UsernamePasswordAuthenticationToken(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(Authority.ROLE_BACKEND_ADMIN));
    }

    private boolean credentialsInvalid(String username, String password){
        return !username.equals(securityProperties.getBackendAdminUser()) || !password.equals(securityProperties.getBackendAdminPassword());
    }

    @Override
    public boolean supports(Class<?> authentication){
        return authentication.equals(BackendAdminUsernamePasswordAuthenticationToken.class);
    }

}
