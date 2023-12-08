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
import com.example.whynotpc.utils.JPACallHandler.Result;
import com.example.whynotpc.utils.NoAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
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

import static com.example.whynotpc.utils.CookieUtils.createJwtCookie;
import static com.example.whynotpc.utils.CookieUtils.extractTokenFromCookie;
import static com.example.whynotpc.utils.JPACallHandler.handleCall;
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

    private User getUserByJwt(String jwt) throws ExpiredJwtException {
        try {
            String username = jwtService.getUsername(jwt);
            return userService.findByUsername(username).orElseThrow(NoAuthenticationException::new);
        } catch (IllegalArgumentException e) {
            throw new NoAuthenticationException("Null sequence in jwt");
        }
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

    private AuthResponse getAuthResponse(Result<String> callResponse) {
        return switch (callResponse.statusCode()) {
            case 200 -> new AuthResponse(200, callResponse.result());
            case 401 -> new AuthResponse(401, "No authentication / bad credentials");
            case 403 -> new AuthResponse(403, "Token is non-existent / revoked / expired");
            default -> new AuthResponse(500, "");
        };
    }

    public AuthResponse register(AuthRequest request) {
        UserDTO userDTO = new UserDTO(request.firstname(), request.lastname(), request.username(), request.email(),
                request.password(), "USER", LocalDateTime.now());
        var response = userService.create(userDTO);

        return switch (response.getStatusCode()) {
            case 201 -> new AuthResponse(200, "Registration success");
            case 400 -> new AuthResponse(400, "Some of the fields might be blank");
            case 409 -> new AuthResponse(409, "Username or e-mail already taken");
            default -> new AuthResponse(500, "");
        };
    }

    public AuthResponse login(AuthRequest request, HttpServletResponse response) {
        var callResponse = handleCall(() -> {
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
            return "Login success";
        });
        return getAuthResponse(callResponse);
    }

    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        var callResponse = handleCall(() -> {
            var accessJwt = extractTokenFromCookie(request, "access_token");
            var refreshJwt = extractTokenFromCookie(request, "refresh_token");
            if (refreshJwt == null)
                throw new NoAuthenticationException("No refresh token is presented");
            var user = getUserByJwt(refreshJwt);
            var refreshToken = refreshTokenRepo.findByToken(refreshJwt).orElseThrow(NoAuthenticationException::new);
            var oldAccessToken = accessTokenRepo.findByToken(accessJwt).orElse(null);
            var newAccessJwt = jwtService.generateToken(user);

            saveAccessToken(newAccessJwt, user, refreshToken, oldAccessToken);
            response.addCookie(createJwtCookie(newAccessJwt, "access_token"));
            return "Token refresh success";
        });
        return getAuthResponse(callResponse);
    }

    public AuthResponse logout(HttpServletRequest request) {
        var accessJwt = extractTokenFromCookie(request, "access_token");
        accessTokenRepo.findByToken(accessJwt).ifPresent(accessTokenRepo::delete);
        return new AuthResponse(204, "Logout success");
    }

    private void terminateOtherSessions(Integer userId, HttpServletRequest request) {
        var currentJwt = extractTokenFromCookie(request, "access_token");
        var tokens = accessTokenRepo.getAllByUserIdExceptCurrent(userId, currentJwt);
        accessTokenRepo.deleteAll(tokens);
    }

    @Transactional
    public AuthResponse terminateOtherSessions(HttpServletRequest request) {
        var response = handleCall(() -> {
            var currentJwt = extractTokenFromCookie(request, "access_token");
            var currentUser = getUserByJwt(currentJwt);

            terminateOtherSessions(currentUser.getId(), request);
            return "Other session termination success";
        });
        return getAuthResponse(response);
    }

    public AuthResponse changePassword(ChangePasswordRequest request, HttpServletRequest servletRequest) {
        var response = handleCall(() -> {
            String refreshJwt = extractTokenFromCookie(servletRequest, "access_token");
            var user = getUserByJwt(refreshJwt);

            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword()))
                throw new NoAuthenticationException("Passwords do not match");

            user.setPassword(passwordEncoder.encode(request.newPassword()));

            userService.update(user.getId(), user);
            terminateOtherSessions(user.getId(), servletRequest);
            return "Password change success";
        });
        return getAuthResponse(response);
    }
}
