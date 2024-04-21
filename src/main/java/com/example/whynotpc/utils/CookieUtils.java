package com.example.whynotpc.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for creating and extracting tokens from cookies.
 */
public class CookieUtils {
    /**
     * Creates a JWT cookie with the specified token and name.
     *
     * @param token      The JWT token to be stored in the cookie.
     * @param cookieName The name of the cookie.
     * @return JWT cookie.
     */
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

    /**
     * Extracts a token from the specified cookie name in the HTTP servlet request.
     *
     * @param request    The HTTP servlet request containing cookies.
     * @param tokenName  The name of the cookie containing the token.
     * @return token extracted from the cookie, or null if not found.
     */
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
