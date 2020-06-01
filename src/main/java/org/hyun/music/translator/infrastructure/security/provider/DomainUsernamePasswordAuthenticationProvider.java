package org.hyun.music.translator.infrastructure.security.provider;

import com.nimbusds.jose.JOSEException;
import org.hyun.music.translator.infrastructure.authenticator.ExternalServiceAuthenticator;
import org.hyun.music.translator.infrastructure.security.TokenService;
import org.hyun.music.translator.model.auth.AuthenticationWithToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

public class DomainUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final TokenService tokenService;

    private final ExternalServiceAuthenticator externalServiceAuthenticator;

    public DomainUsernamePasswordAuthenticationProvider(TokenService tokenService, ExternalServiceAuthenticator externalServiceAuthenticator) {
        this.tokenService = tokenService;
        this.externalServiceAuthenticator = externalServiceAuthenticator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = Optional
                .ofNullable(authentication.getPrincipal())
                .map(e -> (String)e)
                .orElseThrow(() -> new BadCredentialsException("Username is empty"));
        String password = Optional
                .ofNullable(authentication.getCredentials())
                .map(e -> (String)e)
                .orElseThrow(() -> new BadCredentialsException("Password is empty"));
        AuthenticationWithToken resultOfAuthentication = externalServiceAuthenticator.authenticate(username, password);
        try {
            String newToken = tokenService.generateNewAccessToken(resultOfAuthentication);
            resultOfAuthentication.setToken(newToken);
            tokenService.store(newToken, resultOfAuthentication);
        }catch (JOSEException e) {
            throw new AuthenticationException(e.getMessage()){};
        }

        return resultOfAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
