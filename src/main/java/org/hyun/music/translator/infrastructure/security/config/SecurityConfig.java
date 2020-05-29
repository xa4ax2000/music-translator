package org.hyun.music.translator.infrastructure.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyun.music.translator.api.ApiController;
import org.hyun.music.translator.infrastructure.security.ActuatorEndpointAuthenticationFilter;
import org.hyun.music.translator.infrastructure.security.AuthenticationFilter;
import org.hyun.music.translator.model.auth.Authority;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint(){
        return(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Bean
    // this is spring logout successhandler!
    public LogoutSuccessHandler logoutSuccessHandler(){
        return new org.hyun.music.translator.infrastructure.security.LogoutSuccessHandler();
    }

    @Bean
    public ObjectMapper securityObjectMapper(){
        return new ObjectMapper();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception{
        // CORS and CSRF will be enabled by default
        http
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedEntryPoint())
                    .and()
                .sessionManagement()
                    // We will manage the session by user login
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers(actuatorEndpoints())
                        .hasRole(Authority.ROLE_BACKEND_ADMIN)
                    .anyRequest()
                        .authenticated()
                    .and()
                .anonymous()
                    .disable()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher(ApiController.SIGN_OUT_URL))
                    .logoutSuccessHandler(logoutSuccessHandler())
        ;

        http.addFilterBefore(new AuthenticationFilter(authenticationManager(), securityObjectMapper()), BasicAuthenticationFilter.class).
                addFilterBefore(new ActuatorEndpointAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class);
    }

    private String[] actuatorEndpoints(){
        return new String[]{ApiController.AUTOCONFIG_ENDPOINT, ApiController.BEANS_ENDPOINT, ApiController.CONFIGPROPS_ENDPOINT,
                ApiController.ENV_ENDPOINT, ApiController.MAPPINGS_ENDPOINT,
                ApiController.METRICS_ENDPOINT, ApiController.SHUTDOWN_ENDPOINT};
    }

}
