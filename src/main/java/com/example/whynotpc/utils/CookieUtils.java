package com.example.whynotpc.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtils {
    public static Cookie createJwtCookie(String token, String cookieName) {
        Cookie jwtCookie = new Cookie(cookieName, token);

        if (cookieName.equals("access_token"))
            jwtCookie.setMaxAge(604800);
        else
            jwtCookie.setMaxAge(86400);

        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setAttribute("SameSite", "None");
        return jwtCookie;
    }

    public static String extractTokenFromCookie(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return null;

        for (Cookie cookie : cookies)
            if (cookie.getName().equals(tokenName))
                return cookie.getValue();

        return null;
    }
}
