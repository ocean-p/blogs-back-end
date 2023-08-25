package com.example.demoonetomany.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequest {
  @NotBlank
  private String username;

  @NotBlank
  private String password;
}
