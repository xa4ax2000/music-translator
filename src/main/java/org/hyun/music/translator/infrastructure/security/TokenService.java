package org.hyun.music.translator.infrastructure.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.hyun.music.translator.infrastructure.security.config.properties.SecurityProperties;
import org.hyun.music.translator.model.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public class TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    private final Cache restApiAuthTokenCache = CacheManager.getInstance().getCache("restApiAuthTokenCache");
    public static final int HALF_AN_HOUR_IN_MILISECONDS = 30 * 60 * 1000;

    @Autowired
    SecurityProperties securityProperties;

    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILISECONDS)
    public void evictExpiredTokens(){
        LOGGER.info("Evicting expired tokens");
        restApiAuthTokenCache.evictExpiredElements();
    }

    public String generateNewAccessToken(Authentication authentication) throws JOSEException{

        User user = Optional
                .ofNullable(authentication.getPrincipal())
                .map(e -> (User) e)
                .orElseThrow(() -> new BadCredentialsException("User was not found!"));

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("sub", user.getUserId())
                .claim("iat", System.currentTimeMillis())
                .claim("exp", System.currentTimeMillis() + securityProperties.getJwtExpirationInMs())
                .build();
        JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);
        JWEObject jweObject = new JWEObject(jweHeader, new Payload(jwtClaimsSet.toJSONObject()));
        JWEEncrypter encrypter = new DirectEncrypter(securityProperties.getJwtSecret().getBytes());
        jweObject.encrypt(encrypter);
        return jweObject.serialize();
    }

    // TODO: implement refresh token logic
    public String generateNewRefreshToken() {return null;}

    public boolean isValidAccessToken(String authToken){
        ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SimpleSecurityContext> jweKeySource =
                new ImmutableSecret<>(securityProperties.getJwtSecret().getBytes());
        JWEKeySelector<SimpleSecurityContext> jweKeySelector =
                new JWEDecryptionKeySelector<>(JWEAlgorithm.DIR, EncryptionMethod.A256CBC_HS512, jweKeySource);
        jwtProcessor.setJWEKeySelector(jweKeySelector);
        try {
            JWTClaimsSet claims = jwtProcessor.process(authToken, null);
            return true;
        } catch (Exception e){
            LOGGER.error("Failed to process claims: " + e.getMessage());
        }
        return false;
    }

    public void store(String token, Authentication authentication){
        restApiAuthTokenCache.put(new Element(token, authentication));
    }

    public boolean contains(String token){return restApiAuthTokenCache.get(token)!=null;}

    public Authentication retrieve(String token){
        return (Authentication) restApiAuthTokenCache.get(token).getObjectValue();
    }

}
