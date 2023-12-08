package com.example.whynotpc.services;

import com.example.whynotpc.models.response.AuthResponse;
import com.example.whynotpc.persistence.jwt.AccessTokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static com.example.whynotpc.utils.CookieUtils.extractTokenFromCookie;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final AccessTokenRepo accessTokenRepo;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var accessJwt = extractTokenFromCookie(request, "access_token");

        accessTokenRepo.findByToken(accessJwt).ifPresent(accessTokenRepo::delete);
    }
}
