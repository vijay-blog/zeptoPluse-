package com.nexamart.backend.api;

import com.nexamart.backend.api.ApiModels.*;
import com.nexamart.backend.exception.ApiException;
import com.nexamart.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService auth;
  public AuthController(AuthService auth) { this.auth = auth; }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = auth.login(request);
    requireRole(response, "CUSTOMER");
    return response;
  }

  @PostMapping("/customer/register")
  public LoginResponse registerCustomer(@Valid @RequestBody RegisterRequest request) {
    return auth.registerCustomer(request);
  }

  @PostMapping("/refresh")
  public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
    LoginResponse response = auth.refresh(request);
    requireRole(response, "CUSTOMER");
    return response;
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() { return ResponseEntity.noContent().build(); }

  private void requireRole(LoginResponse response, String role) {
    if (!role.equals(response.user().role())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Customer access required.");
    }
  }
}
