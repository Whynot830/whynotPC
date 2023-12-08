package com.example.whynotpc.models.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue
    private Integer id;

    private String token;

    @OneToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "access_token_id")
    private AccessToken accessToken;
}
