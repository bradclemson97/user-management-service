package com.example.usermanagementservice.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A collection of system constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemConstant {

    public static final String API_VERSION = "/v1";
    public static final String API_USER = "/user";
    public static final String API_CURRENT = "/current";
    public static final String API_SOI = "/soi";
    public static final String API_EMAIL = "/email";
    public static final String API_DEACTIVATE = "/deactivate";
    public static final String API_LOGIN = "/login";
    public static final String API_LOCK = "/lock";
}
