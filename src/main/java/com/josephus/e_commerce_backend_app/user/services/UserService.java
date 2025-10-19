package com.josephus.e_commerce_backend_app.user.services;

import com.josephus.e_commerce_backend_app.common.models.ConfirmationToken;
import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import com.josephus.e_commerce_backend_app.model.Users;
import com.josephus.e_commerce_backend_app.user.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.json.JSONException;
import java.util.List;

public interface UserService extends UserDetailsService {

    Boolean hasUserWithEmail(String email);

    Boolean hasUserWithUsername(String username);
//    ResponseEntity<?> authenticate(String username, String password) throws JSONException;

    Boolean hasUserWithPhoneNumber(String phoneNumber);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    List<User> getAllUsers();
    User registerUser(String username, String email, String password);

    String extractUsernameFromToken(String token);

    ConfirmationToken createConfirmationToken(User user);
    ConfirmationToken getConfirmationToken(String token);
    void deleteConfirmationToken(ConfirmationToken token);

    PasswordResetToken createPasswordResetToken(User user);
    PasswordResetToken getPasswordResetToken(String token);
    void deletePasswordResetToken(PasswordResetToken token);

    String verify(User user);

    User loginUser(String email, String password);
    void saveUser(User user);
    User getUserByEmail(String email);
    User getUserById(String adminId);
    String encodePassword(String rawPassword);
}
