package com.digistackBackend.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieUtil {
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());

            Cookie cookie = new Cookie(name, encoded);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(maxAge);

            // Can't directly set SameSite value on Cookie, so use header
            String cookieHeader = String.format(
                    "%s=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=Strict",
                    name, encoded, maxAge
            );

            response.addHeader("Set-Cookie", cookieHeader);
        } catch (Exception e) {
            throw new RuntimeException("Error encoding cookie value", e);
        }
    }
}
