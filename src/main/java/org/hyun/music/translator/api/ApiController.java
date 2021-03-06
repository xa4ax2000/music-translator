package org.hyun.music.translator.api;

public abstract class ApiController {
    private static final String API_PATH = "/api/v1";

    // Authentication APIs (Handled internally via filters)
    public static final String AUTHENTICATE_PATH = API_PATH + "/authenticate";
    public static final String SIGN_IN_URL = AUTHENTICATE_PATH + "/signin";
    public static final String TOKEN_URL = AUTHENTICATE_PATH + "/token";
    public static final String REFRESH_URL = AUTHENTICATE_PATH + "/refresh";
    public static final String SIGN_OUT_URL = AUTHENTICATE_PATH + "/signout";

    // Account APIs
    public static final String ACCOUNT_PATH = API_PATH + "/account";
    public static final String REGISTER_USER_URL = ACCOUNT_PATH + "/register";
    public static final String DELETE_USER_URL = ACCOUNT_PATH + "/delete";

    // Application APIs
    public static final String MUSIC_PATH = API_PATH + "/music";
    public static final String TRANSLATE_URL = MUSIC_PATH + "/translate";

    // Spring Boot Actuator services
    public static final String AUTOCONFIG_ENDPOINT = "/autoconfig";
    public static final String BEANS_ENDPOINT = "/beans";
    public static final String CONFIGPROPS_ENDPOINT = "/configprops";
    public static final String ENV_ENDPOINT = "/env";
    public static final String MAPPINGS_ENDPOINT = "/mappings";
    public static final String METRICS_ENDPOINT = "/metrics";
    public static final String SHUTDOWN_ENDPOINT = "/shutdown";

}
