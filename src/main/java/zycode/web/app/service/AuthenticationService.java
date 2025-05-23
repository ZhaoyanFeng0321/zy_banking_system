package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zycode.web.app.dto.authdto.AuthenticationRequest;
import zycode.web.app.dto.authdto.AuthenticationResponse;
import zycode.web.app.dto.authdto.RefreshTokenRequest;
import zycode.web.app.dto.UserDto;
import zycode.web.app.entity.User;
import zycode.web.app.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistService blacklistService;

    /**
     * Authenticates a user and generates access and refresh tokens (login)
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate with Spring Security
        Authentication authentication  = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Extract username from authenticated principal
        String username = authentication.getName(); // Directly get the username

        // 3. Generate tokens using only the username (no DB fetch)
        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        // 4. (Optional) Fetch user details only if needed for the response

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        // Create and return response
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserDto(user))
                .build();
    }

    /**
     * Refreshes an access token using a valid refresh token
     */
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken) || blacklistService.isBlacklisted(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Extract username and get user details
        String username = jwtService.extractSubject(refreshToken);
        User user = (User) userDetailsService.loadUserByUsername(username);

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        // Blacklist old refresh token
        blacklistService.blacklistToken(refreshToken);

        // Create and return response
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(mapToUserDto(user))
                .build();
    }

    /**
     * Logs out a user by blacklisting their accessToken and refreshToken
     */
    public void logout(String accessToken, String refreshToken) {
        // Remove "Bearer " prefix if present
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        // Blacklist both tokens
        if (accessToken != null) {
            blacklistService.blacklistToken(accessToken);
        }

        if (refreshToken != null) {
            blacklistService.blacklistToken(refreshToken);
        }
    }

    /**
     * Maps a User entity to a UserDto
     */
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}

