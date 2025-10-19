package com.josephus.e_commerce_backend_app.user.services.impl;

import com.josephus.e_commerce_backend_app.common.repositories.ConfirmationTokenRepository;
import com.josephus.e_commerce_backend_app.common.repositories.PasswordResetTokenRepository;
import com.josephus.e_commerce_backend_app.user.repositories.UserRepository;
import com.josephus.e_commerce_backend_app.common.models.ConfirmationToken;
import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import com.josephus.e_commerce_backend_app.model.Users;
import com.josephus.com.ecommercebackend.model.UserRole;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = passwordEncoder;
    }
    public final String TOKEN_PREFIX = "Bearer ";
    public final String HEADER_STRING = "Authorization";

    public Boolean hasUserWithEmail(String email){
        return userRepository.findFirstByEmail(email).isPresent();

    }

    public Boolean hasUserWithUsername(String username){
        return userRepository.findFirstByUsername(username).isPresent();

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> optionalUser = userRepository.findFirstByUsername(username);

        if(optionalUser.isEmpty()) throw  new UsernameNotFoundException("Username not found", null);

        return  new org.springframework.security.core
                .userdetails
                .User(
                        optionalUser.get().getEmail(),
                optionalUser.get().getPassword(),
                new ArrayList<>()
        );
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users registerUser(String username, String email, String password) {

        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(encodedPassword);

        user.setEnabled(false);  // User is not enabled until email confirmation
        user.setRole(UserRole.CUSTOMER);
        return userRepository.save(user);
    }

    //========================= CONFIRMATION  TOKEN ============================
    public ConfirmationToken createConfirmationToken(Users user) {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(UUID.randomUUID().toString());
        confirmationToken.setUser(user);
        confirmationToken.setCreatedDate(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);  // Token expires in 24 hours
        confirmationToken.setExpiryDate(calendar.getTime());

        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public void deleteConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.delete(token);
    }

    public ConfirmationToken getConfirmationToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    //=========================================================================


    //========================= PASSWORD RESET  TOKEN ============================

    @Override
    public PasswordResetToken createPasswordResetToken(Users user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedDate(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);  // Token expires in 24 hours
        token.setExpiryDate(calendar.getTime());

        return passwordResetTokenRepository.save(token);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void deletePasswordResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }

    //=========================================================================


    @Override
    public void saveUser(Users user) {
        userRepository.save(user);
    }

    @Override
    public Users loginUser(String email, String password) {

        Optional<Users> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && bCryptPasswordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional.get(); // Login successful
        } else {
            return null; // Login failed
        }

    }

    @Override
    public Users getUserByEmail(String email) {
        Optional<Users> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
        // return userRepository.findByUsername(email);
    }

    @Override
    public Users getUserById(Long adminId) {
        Optional<Users> userOptional = userRepository.findById(adminId);
        return userOptional.orElse(null);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
}
