package com.example.whynotpc.models.jwt;

import com.example.whynotpc.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an access token entity, which is used for authentication.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "access_tokens")
public class AccessToken {
    /**
     * The unique identifier of the access token.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The access token string.
     */
    private String token;

    /**
     * The associated refresh token. This relationship is excluded from toString method.
     */
    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "accessToken", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    /**
     * The user associated with this access token. This relationship is excluded from toString method.
     */
    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User user;
}
