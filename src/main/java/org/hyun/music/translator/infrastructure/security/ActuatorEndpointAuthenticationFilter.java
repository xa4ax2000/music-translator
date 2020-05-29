package org.hyun.music.translator.infrastructure.security;

import org.hyun.music.translator.api.ApiController;
import org.hyun.music.translator.model.auth.BackendAdminUsernamePasswordAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ActuatorEndpointAuthenticationFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorEndpointAuthenticationFilter.class);
    private AuthenticationManager authenticationManager;
    private Set<String> managementEndpoints;

    public ActuatorEndpointAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        prepareManagementEndpointsSet();
    }

    private void prepareManagementEndpointsSet(){
        managementEndpoints = new HashSet<>();
        managementEndpoints.add(ApiController.AUTOCONFIG_ENDPOINT);
        managementEndpoints.add(ApiController.BEANS_ENDPOINT);
        managementEndpoints.add(ApiController.CONFIGPROPS_ENDPOINT);
        managementEndpoints.add(ApiController.ENV_ENDPOINT);
        managementEndpoints.add(ApiController.MAPPINGS_ENDPOINT);
        managementEndpoints.add(ApiController.METRICS_ENDPOINT);
        managementEndpoints.add(ApiController.SHUTDOWN_ENDPOINT);

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = asHttp(request);
        HttpServletResponse httpResponse = asHttp(response);

        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

        try {
            if (postToActuatorEndpoints(resourcePath)) {
                String username = Optional
                        .ofNullable(httpRequest.getHeader("X-Auth-Username"))
                        .orElseThrow(() -> new BadCredentialsException("Username is empty"));
                String password = Optional
                        .ofNullable(httpRequest.getHeader("X-Auth-Password"))
                        .orElseThrow(() -> new BadCredentialsException("Password is empty"));
                LOGGER.debug("Trying to authenticate user {} for actuator endpoint by X-Auth-Username method", username);
                processActuatorEndpointUsernamePasswordAuthentication(username, password);
            }

            LOGGER.debug("ManagementEndpointAuthenticationFilter is passing request down the filter chain");
            chain.doFilter(request, response);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        }
    }

    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    private boolean postToActuatorEndpoints(String resourcePath) {
        return managementEndpoints.contains(resourcePath);
    }

    private void processActuatorEndpointUsernamePasswordAuthentication(String username, String password) {
        Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(username, password);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    private Authentication tryToAuthenticateWithUsernameAndPassword(String username, String password) {
        BackendAdminUsernamePasswordAuthenticationToken requestAuthentication = new BackendAdminUsernamePasswordAuthenticationToken(username, password);
        return tryToAuthenticate(requestAuthentication);
    }

    private Authentication tryToAuthenticate(Authentication requestAuthentication) {
        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate Backend Admin for provided credentials");
        }
        LOGGER.debug("Backend Admin successfully authenticated");
        return responseAuthentication;
    }

}
