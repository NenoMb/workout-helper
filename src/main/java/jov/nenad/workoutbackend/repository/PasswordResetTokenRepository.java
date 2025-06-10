package jov.nenad.workoutbackend.repository;

import jov.nenad.workoutbackend.entity.PasswordResetToken;
import jov.nenad.workoutbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Find a token by its token string
     * @param token the token string
     * @return an Optional containing the token if found
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Find all tokens for a specific user
     * @param user the user
     * @return a list of tokens
     */
    List<PasswordResetToken> findByUser(User user);
    
    /**
     * Delete all tokens for a specific user
     * @param user the user
     */
    void deleteByUser(User user);
    
    /**
     * Delete all expired tokens
     * @param now the current time
     * @return the number of tokens deleted
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < ?1")
    int deleteAllExpiredTokens(ZonedDateTime now);
}