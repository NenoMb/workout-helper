package jov.nenad.workoutbackend.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jov.nenad.workoutbackend.dto.ForgotPasswordRequest;
import jov.nenad.workoutbackend.dto.LoginRequest;
import jov.nenad.workoutbackend.dto.RegisterRequest;
import jov.nenad.workoutbackend.dto.ResetPasswordRequest;
import jov.nenad.workoutbackend.entity.User;
import jov.nenad.workoutbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) throws MessagingException {
        try {
            userService.forgotPassword(request);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }
}
