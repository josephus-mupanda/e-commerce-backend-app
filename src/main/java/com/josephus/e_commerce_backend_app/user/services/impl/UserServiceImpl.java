package com.josephus.e_commerce_backend_app.user.services.impl;

import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.repositories.ConfirmationTokenRepository;
import com.josephus.e_commerce_backend_app.common.repositories.PasswordResetTokenRepository;
import com.josephus.e_commerce_backend_app.common.utils.JwtUtil;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.repositories.UserRepository;
import com.josephus.e_commerce_backend_app.common.models.ConfirmationToken;
import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
//    public final String TOKEN_PREFIX = "Bearer ";
//    public final String HEADER_STRING = "Authorization";

    @Override
    public Boolean hasUserWithUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public Boolean hasUserWithEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Boolean hasUserWithPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findFirstByUsername(username);

        if(optionalUser.isEmpty()) throw  new UsernameNotFoundException("Username not found", null);

        return  new org.springframework.security.core
                .userdetails
                .User(
                        optionalUser.get().getEmail(),
                optionalUser.get().getPasswordHash(),
                new ArrayList<>()
        );
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(String username, String email, String password) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.setPasswordHash(encodedPassword);

        user.setEnabled(false);  // User is not enabled until email confirmation
        user.setRoles(UserType.CUSTOMER);
        return userRepository.save(user);
    }
    @Override
    public String verify(User user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),user.getPasswordHash()
                )
        );
        if(authentication.isAuthenticated())
            return jwtUtil.generateToken(user);
        return "Failed";
    }

    @Override
    public User loginUser(String email, String password) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && bCryptPasswordEncoder.matches(password, userOptional.get().getPasswordHash())) {
            return userOptional.get(); // Login successful
        } else {
            return null; // Login failed
        }

    }

    @Override
    public String extractUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }

    //========================= CONFIRMATION  TOKEN ============================
    public ConfirmationToken createConfirmationToken(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(UUID.randomUUID().toString());
        confirmationToken.setUser(user);
        confirmationToken.setCreatedDate(LocalDateTime.now());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);  // Token expires in 24 hours
        confirmationToken.setExpiryDate(LocalDateTime.now());

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
    public PasswordResetToken createPasswordResetToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedDate(LocalDateTime.now());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);  // Token expires in 24 hours
        token.setExpiryDate(LocalDateTime.now());

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
    public void saveUser(User user) {
        userRepository.save(user);
    }


    @Override
    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
        // return userRepository.findByUsername(email);
    }

    @Override
    public User getUserById(String adminId) {
        Optional<User> userOptional = userRepository.findById(adminId);
        return userOptional.orElse(null);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
}
