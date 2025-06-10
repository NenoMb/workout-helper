package jov.nenad.workoutbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jov.nenad.workoutbackend.dto.ForgotPasswordRequest;
import jov.nenad.workoutbackend.dto.RegisterRequest;
import jov.nenad.workoutbackend.dto.ResetPasswordRequest;
import jov.nenad.workoutbackend.entity.PasswordResetToken;
import jov.nenad.workoutbackend.entity.User;
import jov.nenad.workoutbackend.repository.PasswordResetTokenRepository;
import jov.nenad.workoutbackend.repository.UserRepository;
import jov.nenad.workoutbackend.util.JwtUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;

    public UserService(UserRepository userRepository,
                       PasswordResetTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent() ||
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Username or email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(username);
        }
        throw new RuntimeException("Invalid credentials");
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Password Reset Request");
        helper.setText("To reset your password, click the link: http://localhost:8080/auth/reset-password?token=" + token);
        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}
