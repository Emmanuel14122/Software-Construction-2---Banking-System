package app.application.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.application.api.request.LoginRequest;
import app.application.api.response.LoginResponse;
import app.domain.models.UserSystem;
import app.domain.services.UserSystemService;
import app.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserSystemService userSystemService;

    public AuthController(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           UserSystemService userSystemService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userSystemService = userSystemService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserSystem user = userSystemService.getUserByUsername(request.getUsername());

        String token = jwtUtil.generateToken(
                user.getIdentification(),
                user.getUsername(),
                user.getSystemRole().name()
        );

        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getSystemRole().name()));
    }
}
