package com.mwalagho.ferdinand.redditclone.service;

import com.mwalagho.ferdinand.redditclone.dto.AuthenticationResponse;
import com.mwalagho.ferdinand.redditclone.dto.LoginRequest;
import com.mwalagho.ferdinand.redditclone.dto.RefreshTokenRequest;
import com.mwalagho.ferdinand.redditclone.dto.RegisterRequest;
import com.mwalagho.ferdinand.redditclone.exceptions.SpringRedditException;
import com.mwalagho.ferdinand.redditclone.model.NotificationEmail;
import com.mwalagho.ferdinand.redditclone.model.User;
import com.mwalagho.ferdinand.redditclone.model.VerificationToken;
import com.mwalagho.ferdinand.redditclone.repository.UserRepository;
import com.mwalagho.ferdinand.redditclone.repository.VerificationTokenRepository;
import com.mwalagho.ferdinand.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService { //contains logic to register user and save to database

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional //new
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        //Method to generate 128 bit verification token after user is saved in DB
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your account",
                user.getEmail(), "Thank you for signing up to ferdinand's springredditclone" + "click on this link to activate" + "\nhttp://localhost:8080/api/auth/accountVerification/" + token));

    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;

    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("Username not found with name" + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return  AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();

    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
         refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
         String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
         return AuthenticationResponse.builder()
                 .authenticationToken(token)
                 .refreshToken(refreshTokenRequest.getRefreshToken())
                 .expiresAt(Instant.now().plusMillis((jwtProvider.getJwtExpirationInMillis())))
                 .username(refreshTokenRequest.getUsername())
                 //TODO (1) I added this username because it was returning null upon refresh
                 .build();
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }
}
