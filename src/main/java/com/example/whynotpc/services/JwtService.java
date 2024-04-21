package com.example.whynotpc.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class responsible for handling JWT (JSON Web Token) related operations.
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    /**
     * Retrieves the signing key used for JWT generation.
     *
     * @return signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Builds a JWT token with the provided extra claims, user details, and expiration.
     *
     * @param extraClaims   Extra claims to include in the token
     * @param userDetails   User details used to generate the token
     * @param expiration    Expiration time for the token
     * @return generated JWT token
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS384)
                .compact();
    }

    /**
     * Generates a JWT token with the provided extra claims and user details.
     *
     * @param extraClaims   Extra claims to include in the token
     * @param userDetails   User details used to generate the token
     * @return generated JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails   User details used to generate the token
     * @return generated JWT string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a refresh JWT token for the given user details.
     *
     * @param userDetails   User details used to generate the refresh token
     * @return generated refresh JWT string
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Parses and retrieves all claims from the provided JWT token.
     *
     * @param token The JWT token to parse
     * @return All claims extracted from the token
     */
    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Retrieves a specific claim from the provided JWT token.
     *
     * @param token             The JWT token to extract the claim from
     * @param claimsTFunction   Function to extract the desired claim from the token
     * @param <T>               Type of the extracted claim
     * @return extracted claim
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = getAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    /**
     * Retrieves the username associated with the provided JWT token.
     *
     * @param token The JWT token to extract the username from
     * @return username extracted from the token
     */
    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date of the provided JWT token.
     *
     * @param token JWT to extract the expiration date from
     * @return expiration date extracted from the token
     */
    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * Checks if the provided JWT token is expired.
     *
     * @param token JWT to check for expiration
     * @return True if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    /**
     * Checks if the provided JWT token is valid for the given user details.
     *
     * @param token         JWT to validate
     * @param userDetails   user details to validate against
     * @return True if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return getUsername(token)
                .equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
