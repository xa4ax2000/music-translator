package org.hyun.music.translator.infrastructure;

import org.hyun.music.translator.infrastructure.authenticator.ExternalWebServiceStub;
import org.hyun.music.translator.model.auth.AuthenticationWithToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticatedExternalWebService extends AuthenticationWithToken {
    private ExternalWebServiceStub externalWebServiceStub;

    public AuthenticatedExternalWebService(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities){
        super(aPrincipal, aCredentials, anAuthorities);
    }

    public ExternalWebServiceStub getExternalWebServiceStub() {
        return externalWebServiceStub;
    }

    public void setExternalWebServiceStub(ExternalWebServiceStub externalWebServiceStub) {
        this.externalWebServiceStub = externalWebServiceStub;
    }
}
