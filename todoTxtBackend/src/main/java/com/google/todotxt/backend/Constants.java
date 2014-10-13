package com.google.todotxt.backend;

/**
 * Contains the client IDs and scopes for allowed clients consuming the helloworld API.
 */
public class Constants {
  public static final String API_EXPLORER_CLIENT_ID = com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;
  public static final String WEB_CLIENT_ID = "710786198471-gkv9vdrgd3sejjkddp6p9f5mk6j5bjak.apps.googleusercontent.com";
  public static final String ANDROID_CLIENT_ID = "710786198471-ihgu65necg8dl8v90hcqr9unbvio2jik.apps.googleusercontent.com";
  public static final String ANDROID_CLIENT_ID_HOME = "710786198471-kphkb3efjej3l6rsc08lj3imb4rc5jg6.apps.googleusercontent.com";
  public static final String IOS_CLIENT_ID = "replace this with your iOS client ID";
  public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;

  public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
}
