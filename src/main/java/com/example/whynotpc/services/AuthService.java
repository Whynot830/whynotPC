package com.example.whynotpc.services;

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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static com.example.whynotpc.models.response.AuthResponse.ok;
import static com.example.whynotpc.utils.CookieUtils.createJwtCookie;
import static com.example.whynotpc.utils.CookieUtils.extractTokenFromCookie;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

/**
 * Service class responsible for authentication-related operations such as user registration, login, logout,
 * token refresh, password changes, and session management.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenRepo accessTokenRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    /**
     * Retrieves the user associated with the given JWT token.
     *
     * @param jwt The JWT token
     * @return The user associated with the JWT token
     * @throws NoAuthenticationException if no user is found for the given JWT token
     */
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

    /**
     * Saves the access token along with the associated refresh token and user in the database.
     *
     * @param jwt          The access token JWT
     * @param user         The user associated with the access token
     * @param refreshToken The associated refresh token
     * @param accessToken  The existing access token (if any)
     */
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

    /**
     * Registers a new user based on the provided UserDTO and saves their information in the database.
     *
     * @param userDTO The UserDTO containing user information
     * @param file    The profile picture file (optional)
     * @return AuthResponse indicating the success of the registration process
     */
    public AuthResponse register(UserDTO userDTO, MultipartFile file) {
        UserDTO acceptedDTO = new UserDTO(userDTO.firstname(), userDTO.lastname(), userDTO.username(), userDTO.email(),
                userDTO.password(), "USER", LocalDateTime.now());
        userService.create(acceptedDTO, file);
        return ok();
    }

    /**
     * Authenticates a user based on the provided credentials and generates access and refresh tokens.
     *
     * @param userDTO  The UserDTO containing user credentials
     * @param response The HttpServletResponse to set cookies in
     * @return AuthResponse indicating the success of the login process
     * @throws NoAuthenticationException if authentication fails
     */
    public AuthResponse login(UserDTO userDTO, HttpServletResponse response) {
        String usernameOrEmail = isNullOrBlank(userDTO.username()) ? userDTO.email() : userDTO.username();
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, userDTO.password()));
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

    /**
     * Refreshes the access token using the refresh token.
     *
     * @param request  The HttpServletRequest containing the refresh token
     * @param response The HttpServletResponse to set cookies in
     * @return AuthResponse indicating the success of the token refresh process
     */
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

    /**
     * Logs out the user by deleting the access token associated with their session.
     *
     * @param request The HttpServletRequest containing the access token
     * @return AuthResponse indicating the success of the logout process
     */
    public AuthResponse logout(HttpServletRequest request) {
        var accessJwt = extractTokenFromCookie(request, "access_token");
        accessTokenRepo.findByToken(accessJwt).ifPresent(accessTokenRepo::delete);
        return ok();
    }

    /**
     * Terminates all other sessions for the user except the current one.
     *
     * @param userId  The ID of the user whose sessions are to be terminated
     * @param request The HttpServletRequest containing the access token
     */
    private void terminateOtherSessions(Long userId, HttpServletRequest request) {
        var currentJwt = extractTokenFromCookie(request, "access_token");
        var tokens = accessTokenRepo.getAllByUserIdExceptCurrent(userId, currentJwt);
        accessTokenRepo.deleteAll(tokens);
    }

    /**
     * Terminates all other sessions for the user except the current one.
     *
     * @param request The HttpServletRequest containing the access token
     * @return AuthResponse indicating the success of terminating other sessions
     */
    @Transactional
    public AuthResponse terminateOtherSessions(HttpServletRequest request) {
        var currentJwt = extractTokenFromCookie(request, "access_token");
        var currentUser = getUserByJwt(currentJwt);

        terminateOtherSessions(currentUser.getId(), request);
        return ok();
    }

    /**
     * Changes the password for the user and terminates other sessions.
     *
     * @param request       The ChangePasswordRequest containing the old and new passwords
     * @param servletRequest The HttpServletRequest containing the access token
     * @return AuthResponse indicating the success of the password change process
     * @throws NoAuthenticationException if the old password does not match the current password
     */
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
