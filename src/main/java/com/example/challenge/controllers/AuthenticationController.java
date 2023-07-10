package com.example.challenge.controllers;
import com.example.challenge.services.UserService;
import com.example.challenge.dto.LoginRequestDTO;
import com.example.challenge.dto.UserRegistrationRequestDTO;
import com.example.challenge.dto.UserRegistrationResponseDTO;
import com.example.challenge.config.JwtUtil;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class AuthenticationController {
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<String> login
            (@RequestBody LoginRequestDTO authenticationRequest) throws Exception {
        final Authentication auth = authenticate(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtUtil.generateToken(authenticationRequest.getEmail());
        return ResponseEntity.ok(token);
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<UserRegistrationResponseDTO> registerUser(
            @Valid @RequestBody UserRegistrationRequestDTO request
    ) {
        UserRegistrationResponseDTO user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
