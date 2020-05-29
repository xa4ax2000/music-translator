package org.hyun.music.translator.model.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class BackendAdminUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public BackendAdminUsernamePasswordAuthenticationToken(Object aPrincipal, Object aCredentials){
        super(aPrincipal, aCredentials);
    }

    public BackendAdminUsernamePasswordAuthenticationToken(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities){
        super(aPrincipal, aCredentials, anAuthorities);
    }

}
