package com.example.demoonetomany.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoonetomany.exception.TokenRefreshException;
import com.example.demoonetomany.model.ERole;
import com.example.demoonetomany.model.RefreshToken;
import com.example.demoonetomany.model.Role;
import com.example.demoonetomany.model.User;
import com.example.demoonetomany.payload.request.LoginRequest;
import com.example.demoonetomany.payload.request.SignUpRequest;
import com.example.demoonetomany.payload.request.TokenRefreshRequest;
import com.example.demoonetomany.payload.response.JwtResponse;
import com.example.demoonetomany.payload.response.MessageResponse;
import com.example.demoonetomany.payload.response.TokenRefreshResponse;
import com.example.demoonetomany.repository.RoleRepository;
import com.example.demoonetomany.repository.UserRepository;
import com.example.demoonetomany.security.jwt.JwtUtils;
import com.example.demoonetomany.security.services.RefreshTokenService;
import com.example.demoonetomany.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(
    @Valid @RequestBody LoginRequest loginRequest
  ){
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String jwt = jwtUtils.generateJwtToken(userDetails);
    List<String> roles = userDetails.getAuthorities().stream().map(
      item -> item.getAuthority()).collect(Collectors.toList());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());  
    return ResponseEntity.ok(new JwtResponse(
      jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(), 
      userDetails.getEmail(), roles
    ));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request){
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
      .map(refreshTokenService::verifyExpiration)
      .map(RefreshToken::getUser)
      .map(user -> {
        String token = jwtUtils.generateTokenFromUsername(user.getUsername());
        return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
      }).orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
    if(userRepository.existsByUsername(signUpRequest.getUsername())){
      return ResponseEntity.badRequest().body(
        new MessageResponse("Error: Username is already taken!")
      );
    }

    if(userRepository.existsByEmail(signUpRequest.getEmail())){
      return ResponseEntity.badRequest().body(
        new MessageResponse("Error: Email is already taken!")
      );
    }

    //Create new user account
    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), 
      encoder.encode(signUpRequest.getPassword()));
    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if(strRoles == null){
      Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
        () -> new RuntimeException("Error: Role is Not Found!"));
      roles.add(userRole);
    }
    else{
      strRoles.forEach(role -> {
        switch(role){
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(
              () -> new RuntimeException("Error: Role is Not Found!"));
            roles.add(adminRole);
            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR).orElseThrow(
              () -> new RuntimeException("Error: Role is Not Found!"));
            roles.add(modRole);
            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
              () -> new RuntimeException("Error: Role is Not Found!"));
            roles.add(userRole);
            break;
        }
      });
    }
    user.setRoles(roles);
    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
