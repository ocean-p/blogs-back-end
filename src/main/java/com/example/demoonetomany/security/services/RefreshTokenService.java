package com.example.demoonetomany.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demoonetomany.exception.TokenRefreshException;
import com.example.demoonetomany.model.RefreshToken;
import com.example.demoonetomany.model.User;
import com.example.demoonetomany.repository.RefreshTokenRepository;
import com.example.demoonetomany.repository.UserRepository;

@Service
public class RefreshTokenService {
  @Value("${bezkoder.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  public Optional<RefreshToken> findByToken(String token){
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken createRefreshToken(Long userId){
    Optional<User> user = userRepository.findById(userId);

    if(!user.isPresent()){
      throw new TokenRefreshException("", "User is not found !");
    }

    Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUser(user.get());
    if(refreshToken.isPresent()){
      RefreshToken _refreshToken = refreshToken.get();
      _refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
      _refreshToken.setToken(UUID.randomUUID().toString());
      _refreshToken = refreshTokenRepository.save(_refreshToken);
      return _refreshToken;
    } else{
      RefreshToken _refreshToken = new RefreshToken();
      _refreshToken.setUser(userRepository.findById(userId).get());
      _refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
      _refreshToken.setToken(UUID.randomUUID().toString());
      _refreshToken = refreshTokenRepository.save(_refreshToken);
      return _refreshToken;
    }
  }

  public RefreshToken verifyExpiration(RefreshToken token){
    if(token.getExpiryDate().compareTo(Instant.now()) < 0){
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }

    return token;
  }

  @Transactional
  public int deleteByUserId(Long userId){
    return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
  }
}
