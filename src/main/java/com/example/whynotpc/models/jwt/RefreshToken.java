package com.example.whynotpc.models.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a refresh token entity, which is used for refreshing access tokens.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {
    /**
     * The unique identifier of the refresh token.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The refresh token string.
     */
    private String token;

    /**
     * The associated access token. This relationship is excluded from toString method.
     */
    @OneToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "access_token_id")
    private AccessToken accessToken;
}
