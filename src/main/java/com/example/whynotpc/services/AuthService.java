package com.example.whynotpc.services;

import com.example.whynotpc.models.auth.AuthRequest;
import com.example.whynotpc.models.auth.ChangePasswordRequest;
import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.jwt.AccessToken;
import com.example.whynotpc.models.jwt.RefreshToken;
import com.example.whynotpc.models.response.AuthResponse;
import com.example.whynotpc.models.users.User;
import com.example.whynotpc.persistence.jwt.AccessTokenRepo;
import com.example.whynotpc.persistence.jwt.RefreshTokenRepo;
import com.example.whynotpc.utils.NoAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.whynotpc.models.response.AuthResponse.ok;
import static com.example.whynotpc.utils.CookieUtils.createJwtCookie;
import static com.example.whynotpc.utils.CookieUtils.extractTokenFromCookie;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenRepo accessTokenRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    private User getUserByJwt(String jwt) {
        var accessToken = accessTokenRepo.findByToken(jwt).orElse(null);
        var refreshToken = refreshTokenRepo.findByToken(jwt).orElse(null);
        if (accessToken == null && refreshToken == null)
            throw new NoAuthenticationException();

        String username;
        if (accessToken != null) username = jwtService.getUsername(accessToken.getToken());
        else username = jwtService.getUsername(refreshToken.getToken());

        return userService.findByUsername(username).orElseThrow(NoAuthenticationException::new);
    }

    private void saveAccessToken(String jwt, User user, RefreshToken refreshToken, AccessToken accessToken) {
        var token = AccessToken.builder()
                .token(jwt)
                .user(user)
                .build();
        refreshToken.setAccessToken(token);
        if (accessToken != null)
            token.setId(accessToken.getId());

        accessTokenRepo.save(token);
    }

    public AuthResponse register(AuthRequest request) {
        UserDTO userDTO = new UserDTO(request.firstname(), request.lastname(), request.username(), request.email(),
                request.password(), "USER", LocalDateTime.now());
        userService.create(userDTO);
        return ok();
    }

    public AuthResponse login(AuthRequest request, HttpServletResponse response) {
        String usernameOrEmail = isNullOrBlank(request.username()) ? request.email() : request.username();
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, request.password()));
        } catch (BadCredentialsException exception) {
            throw new NoAuthenticationException("Bad credentials");
        }
        var user = userService
                .findByUsername(usernameOrEmail)
                .orElseGet(
                        () -> userService.findByEmail(usernameOrEmail)
                                .orElseThrow(NoAuthenticationException::new)
                );
        String accessJwt = jwtService.generateToken(user);
        String refreshJwt = jwtService.generateRefreshToken(user);
        var accessToken = accessTokenRepo.findByToken(accessJwt).orElse(null);
        var refreshToken = refreshTokenRepo.findByToken(refreshJwt).orElse(null);
        if (accessToken != null || refreshToken != null) {
            accessJwt = jwtService.generateToken(user);
            refreshJwt = jwtService.generateRefreshToken(user);
        }
        refreshToken = refreshTokenRepo.save(
                RefreshToken.builder()
                        .token(refreshJwt)
                        .build());
        saveAccessToken(accessJwt, user, refreshToken, null);

        response.addCookie(createJwtCookie(accessJwt, "access_token"));
        response.addCookie(createJwtCookie(refreshJwt, "refresh_token"));
        return ok();
    }

    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        var accessJwt = extractTokenFromCookie(request, "access_token");
        var refreshJwt = extractTokenFromCookie(request, "refresh_token");
        var user = getUserByJwt(refreshJwt);
        var refreshToken = refreshTokenRepo.findByToken(refreshJwt).orElseThrow(NoAuthenticationException::new);
        var oldAccessToken = accessTokenRepo.findByToken(accessJwt).orElse(null);
        var newAccessJwt = jwtService.generateToken(user);

        saveAccessToken(newAccessJwt, user, refreshToken, oldAccessToken);
        response.addCookie(createJwtCookie(newAccessJwt, "access_token"));
        return ok();
    }

    public AuthResponse logout(HttpServletRequest request) {
        var accessJwt = extractTokenFromCookie(request, "access_token");
        accessTokenRepo.findByToken(accessJwt).ifPresent(accessTokenRepo::delete);
        return ok();
    }

    private void terminateOtherSessions(Long userId, HttpServletRequest request) {
        var currentJwt = extractTokenFromCookie(request, "access_token");
        var tokens = accessTokenRepo.getAllByUserIdExceptCurrent(userId, currentJwt);
        accessTokenRepo.deleteAll(tokens);
    }

    @Transactional
    public AuthResponse terminateOtherSessions(HttpServletRequest request) {
        var currentJwt = extractTokenFromCookie(request, "access_token");
        var currentUser = getUserByJwt(currentJwt);

        terminateOtherSessions(currentUser.getId(), request);
        return ok();
    }

    public AuthResponse changePassword(ChangePasswordRequest request, HttpServletRequest servletRequest) {
        String refreshJwt = extractTokenFromCookie(servletRequest, "access_token");
        var user = getUserByJwt(refreshJwt);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword()))
            throw new NoAuthenticationException("Passwords do not match");

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userService.update(user.getId(), user);
        terminateOtherSessions(user.getId(), servletRequest);
        return ok();
    }
}
