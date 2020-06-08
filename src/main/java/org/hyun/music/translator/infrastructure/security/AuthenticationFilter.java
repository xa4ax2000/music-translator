package org.hyun.music.translator.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyun.music.translator.api.ApiController;
import org.hyun.music.translator.model.auth.payload.JwtAuthenticationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthenticationFilter extends GenericFilterBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String TOKEN_SESSION_KEY = "token";
    private static final String USER_SESSION_KEY = "user";

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper){
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = asHttp(request);
        HttpServletResponse httpResponse = asHttp(response);
        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

        try{
            if(postToAuthentication(httpRequest, resourcePath)){
                String username = Optional
                        .ofNullable(httpRequest.getHeader("X-Auth-Username"))
                        .orElseThrow(() -> new BadCredentialsException("Username is empty"));
                String password = Optional
                        .ofNullable(httpRequest.getHeader("X-Auth-Password"))
                        .orElseThrow(() -> new BadCredentialsException("Password is empty"));
                LOGGER.debug("Trying to authenticate user {} by X-Auth-Username method", username);
                processUsernamePasswordAuthentication(httpResponse, username, password);
            }else{
                String token = Optional
                        .ofNullable(httpRequest.getHeader("X-Auth-Token"))
                        .orElseThrow(() -> new BadCredentialsException("Token is missing"));
                if(postToRefresh(httpRequest, resourcePath)){
                    LOGGER.debug("Trying to refresh user {} by X-Auth-Token method", token);
                    processRefreshAuthentication(token);
                }else{
                    LOGGER.debug("Trying to authenticate user {} by X-Auth-Token method", token);
                    processTokenAuthentication(token);
                }
            }

            LOGGER.debug("AuthenticationFilter is passing request down the filter chain");
            addSessionContextToLogging();
            chain.doFilter(request, response);
        }catch(InternalAuthenticationServiceException e){
            SecurityContextHolder.clearContext();
            LOGGER.error("Internal authentication service exception", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }catch (AuthenticationException e){
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }finally {
            MDC.remove(TOKEN_SESSION_KEY);
            MDC.remove(USER_SESSION_KEY);
        }
    }

    private void addSessionContextToLogging() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tokenSetInMDC = Optional.ofNullable(authentication)
                .map(auth -> auth.getDetails().toString())
                .filter(s -> !s.isEmpty())
                .map(token ->{
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String encryptedToken = encoder.encode(token);
                    MDC.put(TOKEN_SESSION_KEY, encryptedToken);
                    return encryptedToken;
                }).orElseGet(() -> {
                    MDC.put(TOKEN_SESSION_KEY, "EMPTY");
                    return "EMPTY";
                });

        String userValueSetInMDC = Optional.ofNullable(authentication)
                .map(auth -> auth.getPrincipal().toString())
                .filter(s -> !s.isEmpty())
                .map(userValue -> {
                    MDC.put(USER_SESSION_KEY, authentication.getPrincipal().toString());
                    return userValue;
                }).orElseGet(() -> {
                    MDC.put(USER_SESSION_KEY, "EMPTY");
                    return "EMPTY";
                });
        LOGGER.debug("Token set in MDC: " + tokenSetInMDC + " | User set in MDC: " + userValueSetInMDC);
    }

    private HttpServletRequest asHttp(ServletRequest request){
        return (HttpServletRequest) request;
    }

    private HttpServletResponse asHttp(ServletResponse response){
        return (HttpServletResponse) response;
    }

    private boolean postToAuthentication(HttpServletRequest httpRequest, String resourcePath){
        return ApiController.SIGN_IN_URL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
    }

    // TODO: implement refresh token logic
    private boolean postToRefresh(HttpServletRequest httpRequest, String resourcePath){
        //return ApiController.REFRESH_URL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
        return false;
    }

    private void processUsernamePasswordAuthentication(HttpServletResponse httpResponse, String username, String password) throws IOException {
        Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(username, password);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse(resultOfAuthentication.getDetails().toString());
        String tokenJsonResponse = objectMapper.writeValueAsString(jwtAuthenticationResponse);
        httpResponse.addHeader("Content-Type", "application/json");
        httpResponse.getWriter().print(tokenJsonResponse);
    }

    private Authentication tryToAuthenticateWithUsernameAndPassword(String username, String password){
        UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(username, password);
        return tryToAuthenticate(requestAuthentication);
    }

    private void processTokenAuthentication(String token){
        Authentication resultOfAuthentication = tryToAuthenticateWithToken(token);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    private Authentication tryToAuthenticateWithToken(String token){
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
        return tryToAuthenticate(requestAuthentication);
    }

    private Authentication tryToAuthenticate(Authentication requestAuthentication){
        Authentication authenticationResponse = authenticationManager.authenticate(requestAuthentication);
        Optional.ofNullable(authenticationResponse)
                .map(Authentication::isAuthenticated)
                .map(authenticated -> authenticated ? Optional.of(true) : Optional.empty())
                .orElseThrow(() -> new InternalAuthenticationServiceException("Unable to authenticate user with provided credentials"));
        LOGGER.debug("User successfully authenticated");
        return authenticationResponse;
    }

    private void processRefreshAuthentication(String token){
        Authentication resultOfAuthentication = tryToAuthenticateWithRefreshToken(token);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    private Authentication tryToAuthenticateWithRefreshToken(String token){
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
        return tryToAuthenticate(requestAuthentication);
    }

}
