package com.example.whynotpc.models.jwt;

import com.example.whynotpc.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "access_tokens")
public class AccessToken {
    @Id
    @GeneratedValue
    private Long id;

    private String token;
    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "accessToken", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User user;
}
