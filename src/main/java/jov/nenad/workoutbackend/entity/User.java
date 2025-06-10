package jov.nenad.workoutbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    // Helper method to add a token
    public void addPasswordResetToken(PasswordResetToken token) {
        passwordResetTokens.add(token);
        token.setUser(this);
    }

    // Helper method to remove a token
    public void removePasswordResetToken(PasswordResetToken token) {
        passwordResetTokens.remove(token);
        token.setUser(null);
    }
}
