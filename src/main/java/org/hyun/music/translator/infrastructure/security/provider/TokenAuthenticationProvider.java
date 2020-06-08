package org.hyun.music.translator.infrastructure.security.provider;

import org.hyun.music.translator.infrastructure.security.TokenService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenService tokenService;

    public TokenAuthenticationProvider(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return Optional.ofNullable(authentication.getPrincipal())
                .map(str -> (String) str)
                .map(token -> {
                    if (tokenService.isValidAccessToken(token) && tokenService.contains(token)) {
                        return tokenService.retrieve(token);
                    }
                    return null;
                }).orElseThrow(()->new BadCredentialsException("Could not find user tied to the token"));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
