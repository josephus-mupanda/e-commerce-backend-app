package com.josephus.com.ecommercebackend.service;

import com.josephus.com.ecommercebackend.model.ConfirmationToken;
import com.josephus.com.ecommercebackend.model.PasswordResetToken;
import com.josephus.com.ecommercebackend.model.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface UserService extends UserDetailsService {

    Boolean hasUserWithEmail(String email);

    Boolean hasUserWithUsername(String username);
//    ResponseEntity<?> authenticate(String username, String password) throws JSONException;

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    List<Users> getAllUsers();
    Users registerUser(String username, String email, String password);

    ConfirmationToken createConfirmationToken(Users user);
    ConfirmationToken getConfirmationToken(String token);
    void deleteConfirmationToken(ConfirmationToken token);

    PasswordResetToken createPasswordResetToken(Users user);
    PasswordResetToken getPasswordResetToken(String token);
    void deletePasswordResetToken(PasswordResetToken token);

    Users loginUser(String email, String password);
    void saveUser(Users user);
    Users getUserByEmail(String email);
    Users getUserById(Long adminId);

    String encodePassword(String rawPassword);
}
