package org.hyun.music.translator.model.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collection;

public class AuthenticationWithToken extends PreAuthenticatedAuthenticationToken {
    private String expiration;

    public AuthenticationWithToken(Object aPrincipal, Object aCredentials){
        super(aPrincipal, aCredentials);
    }

    public AuthenticationWithToken(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities){
        super(aPrincipal, aCredentials, anAuthorities);
    }

    public void setToken(String token){
        setDetails(token);
    }

    public String getToken() {
        return (String) getDetails();
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

}
