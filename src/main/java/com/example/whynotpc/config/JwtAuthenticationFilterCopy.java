//package com.example.whynotpc.config;
//
//import com.example.whynotpc.models.jwt.AccessToken;
//import com.example.whynotpc.models.users.User;
//import com.example.whynotpc.persistence.jwt.AccessTokenRepo;
//import com.example.whynotpc.persistence.jwt.RefreshTokenRepo;
//import com.example.whynotpc.services.JwtService;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilterCopy extends OncePerRequestFilter {
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//    private final AccessTokenRepo accessTokenRepo;
//    private final RefreshTokenRepo refreshTokenRepo;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain) throws ServletException, IOException {
//        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        final String jwt;
//        String username;
//        if (authHeader == null || !authHeader.startsWith("Bearer ")
//                || authHeader.equals("Bearer null") || request.getServletPath().equals("/api/auth/refresh-token")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwt = authHeader.substring(7);
//
//
//        try {
//            username = jwtService.getUsername(jwt);
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                var userDetails = this.userDetailsService.loadUserByUsername(username);
//                var isTokenValid = accessTokenRepo.findByToken(jwt)
//                        .map(token -> jwtService.isTokenValid(token.getToken(), userDetails))
//                        .orElse(false);
//                if (isTokenValid) {
//                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//                    authenticationToken.setDetails(
//                            new WebAuthenticationDetailsSource().buildDetails(request)
//                    );
//                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                    if (request.getServletPath().equals("/api/auth/change-password"))
//                        request.setAttribute("current_jwt", jwt);
//                }
//            }
//        } catch (ExpiredJwtException e) {
//            var refreshToken = accessTokenRepo.findByToken(jwt).map(AccessToken::getRefreshToken).orElse(null);
//            if (refreshToken == null)
//                filterChain.doFilter(request, response);
//
//            else {
//                username = jwtService.getUsername(refreshToken.getToken());
//                var user = userDetailsService.loadUserByUsername(username);
//                var newToken = jwtService.generateToken(user);
//
//                var oldAccessToken = accessTokenRepo.findByToken(refreshToken.getAccessToken().getToken()).orElseThrow();
//                var newAccessToken = accessTokenRepo.save(AccessToken.builder()
//                        .id(oldAccessToken.getId())
//                        .token(newToken)
//                        .user((User) user)
//                        .build());
//                refreshToken.setAccessToken(newAccessToken);
//                refreshTokenRepo.save(refreshToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}
