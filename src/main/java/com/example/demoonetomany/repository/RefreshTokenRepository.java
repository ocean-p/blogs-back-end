package com.example.demoonetomany.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.demoonetomany.model.RefreshToken;
import com.example.demoonetomany.model.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByUser(User user);

  @Modifying
  int deleteByUser(User user);
}
