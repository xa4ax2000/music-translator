package org.hyun.music.translator.infrastructure.authenticator;

import org.hyun.music.translator.model.auth.AuthenticationWithToken;

public interface ExternalServiceAuthenticator {
    AuthenticationWithToken authenticate(Object username, Object password);
}
