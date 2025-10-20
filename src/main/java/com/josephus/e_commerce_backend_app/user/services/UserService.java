package com.josephus.e_commerce_backend_app.user.services;

import com.josephus.e_commerce_backend_app.common.models.ConfirmationToken;
import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import com.josephus.e_commerce_backend_app.user.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.json.JSONException;
import java.util.List;

public interface UserService extends UserDetailsService {

    Boolean hasUserWithEmail(String email);

    Boolean hasUserWithUsername(String username);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    List<User> getAllUsers();

    String extractUsernameFromToken(String token);

    ConfirmationToken createConfirmationToken(User user);
    ConfirmationToken getConfirmationToken(String token);
    void deleteConfirmationToken(ConfirmationToken token);

    PasswordResetToken createPasswordResetToken(User user);
    PasswordResetToken getPasswordResetToken(String token);
    void deletePasswordResetToken(PasswordResetToken token);

    void saveUser(User user);

    // -------------------- REGISTER USER --------------------
    User registerUser(String username, String email, String password);

    // -------------------- LOGIN --------------------
    String verify(String email, String rawPassword);

    void invalidateToken(String token);

    User getUserByEmail(String email);
    User getUserById(String adminId);
    String encodePassword(String rawPassword);

    User getUserByUsername(String username);

    User getUserFromToken(String bearerToken);
}
