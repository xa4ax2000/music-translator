package org.hyun.music.translator.infrastructure.authenticator;

import org.hyun.music.translator.infrastructure.AuthenticatedExternalWebService;
import org.hyun.music.translator.model.auth.AuthenticationWithToken;
import org.hyun.music.translator.model.auth.User;
import org.hyun.music.translator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.Optional;

public class DatabaseAuthenticator implements ExternalServiceAuthenticator {

    @Autowired
    private UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public AuthenticationWithToken authenticate(Object username, Object password) {
        String un = Optional
                .ofNullable(username)
                .map(e -> (String) e)
                .orElseThrow(() -> new BadCredentialsException("Username was not found"));
        User user = userRepository.findByUsername(un)
                .orElseThrow(() -> new BadCredentialsException("Could not find user with username: " + un));
        String pw = Optional
                .ofNullable(password)
                .map(e -> (String) e)
                .orElseThrow(() -> new BadCredentialsException("Password was not found"));
        verifyPassword(pw, user.getPassword());
        // If authentication succeeded then create wrapper with proper Principal (Entire User object) and GrantedAuthorities.
        // GrantedAuthorities:
        Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities();
        AuthenticatedExternalWebService authenticatedExternalWebService =
                new AuthenticatedExternalWebService(user, null, grantedAuthorities);
        authenticatedExternalWebService.setExternalWebServiceStub(new DatabaseStub());
        return authenticatedExternalWebService;
    }

    private void verifyPassword(String password, String encryptedPassword) throws BadCredentialsException{
        if(!bCryptPasswordEncoder.matches(password, encryptedPassword)){
            throw new BadCredentialsException("Invalid password");
        }
    }

}
