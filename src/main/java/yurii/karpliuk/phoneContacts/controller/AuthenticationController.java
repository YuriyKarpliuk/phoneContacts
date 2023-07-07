package yurii.karpliuk.phoneContacts.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yurii.karpliuk.phoneContacts.dto.request.AuthenticationRequest;
import yurii.karpliuk.phoneContacts.dto.request.RegisterRequest;
import yurii.karpliuk.phoneContacts.dto.response.AuthenticationResponse;
import yurii.karpliuk.phoneContacts.security.service.AuthenticationService;

@CrossOrigin(origins = "*", maxAge = 3600)

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
