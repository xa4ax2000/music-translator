package org.hyun.music.translator.infrastructure.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyun.music.translator.api.ApiController;
import org.hyun.music.translator.infrastructure.authenticator.DatabaseAuthenticator;
import org.hyun.music.translator.infrastructure.authenticator.ExternalServiceAuthenticator;
import org.hyun.music.translator.infrastructure.security.ActuatorEndpointAuthenticationFilter;
import org.hyun.music.translator.infrastructure.security.AuthenticationFilter;
import org.hyun.music.translator.infrastructure.security.TokenService;
import org.hyun.music.translator.infrastructure.security.provider.BackendAdminUsernamePasswordAuthenticationProvider;
import org.hyun.music.translator.infrastructure.security.provider.DomainUsernamePasswordAuthenticationProvider;
import org.hyun.music.translator.infrastructure.security.provider.TokenAuthenticationProvider;
import org.hyun.music.translator.model.auth.Authority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;

@Configuration
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

    @Bean
    public AuthenticationProvider domainUsernamePasswordAuthenticationProvider(){
        return new DomainUsernamePasswordAuthenticationProvider(tokenService(), externalServiceAuthenticator());
    }

    @Bean
    public AuthenticationProvider backendAdminUsernamePasswordAuthenticationProvider(){
        return new BackendAdminUsernamePasswordAuthenticationProvider();
    }

    @Bean
    public TokenService tokenService(){ return new TokenService();}

    @Bean
    public ExternalServiceAuthenticator externalServiceAuthenticator() { return new DatabaseAuthenticator();}

    @Bean
    public AuthenticationProvider tokenAuthenticationProvider(){
        return new TokenAuthenticationProvider(tokenService());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(ApiController.REGISTER_USER_URL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        // CORS will be enabled by default
        http
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedEntryPoint())
                    .and()
                .sessionManagement()
                    // We will manage the session by user login
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers(actuatorEndpoints())
                        .hasRole(Authority.BACKEND_ADMIN)
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(domainUsernamePasswordAuthenticationProvider()).
                authenticationProvider(backendAdminUsernamePasswordAuthenticationProvider()).
                authenticationProvider(tokenAuthenticationProvider());

    }
}
