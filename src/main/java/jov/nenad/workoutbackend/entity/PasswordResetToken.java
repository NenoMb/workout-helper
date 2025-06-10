package jov.nenad.workoutbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // Constructor with user and token
    public PasswordResetToken(User user, String token, LocalDateTime expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    // Utility method to check if token is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
