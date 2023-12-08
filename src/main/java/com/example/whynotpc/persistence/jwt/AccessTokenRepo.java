package com.example.whynotpc.persistence.jwt;

import com.example.whynotpc.models.jwt.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer> {
    Optional<AccessToken> findByToken(String token);
    @Modifying
    @Query("DELETE FROM AccessToken a WHERE a.user.id = :userId AND a.token != :currToken")
    void deleteAllExceptCurrentByUserId(Integer userId, String currToken);
    @Query("SELECT a FROM AccessToken a WHERE a.user.id = :userId AND a.token != :currToken")
    List<AccessToken> getAllByUserIdExceptCurrent(Integer userId, String currToken);

}
