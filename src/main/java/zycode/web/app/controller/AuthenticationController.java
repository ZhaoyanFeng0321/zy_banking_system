package zycode.web.app.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import zycode.web.app.dto.authdto.AuthenticationRequest;
import zycode.web.app.dto.authdto.AuthenticationResponse;
import zycode.web.app.dto.authdto.RefreshTokenRequest;
import zycode.web.app.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final boolean isProduction = false;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
             /*Replace:
                    return ResponseEntity.ok()
                        .header("Authorization", "Bearer " + response.getAccessToken()).body(response);
            */
            // user HttpOnly cookies to store token
            // Create cookies
            ResponseCookie accessCookie = ResponseCookie.from("access_token", response.getAccessToken())
                    .httpOnly(true)
                    .secure(isProduction)  // Use in production with HTTPS
                    .sameSite("Strict")
                    .maxAge(900)   // 15 minutes
                    .path("/")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                    .httpOnly(true)
                    .secure(isProduction)  // Use in production with HTTPS
                    .sameSite("Strict")
                    .maxAge(86400) // 1 day
                    .path("/auth/refresh") // Restrict to refresh endpoint only
                    .build();

            // Remove tokens from response body for added security
            response.setAccessToken(null);
            response.setRefreshToken(null);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);


        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during authentication");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody HttpServletRequest request) {
            //AuthenticationResponse response = authenticationService.refreshToken(request);
//            return ResponseEntity.ok()
//                    .header("Authorization", "Bearer " + response.getAccessToken())
//                    .body(response);
// Extract refresh token from cookie
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refresh_token".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Refresh token not found");
            }

            try {
                RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
                refreshRequest.setRefreshToken(refreshToken);
                AuthenticationResponse response = authenticationService.refreshToken(refreshRequest);

                // Create new cookies with updated tokens
                ResponseCookie accessCookie = ResponseCookie.from("access_token", response.getAccessToken())
                        .httpOnly(true)
                        .secure(isProduction)
                        .sameSite("Strict")
                        .maxAge(900)
                        .path("/")
                        .build();

                ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                        .httpOnly(true)
                        .secure(isProduction)
                        .sameSite("Strict")
                        .maxAge(86400)
                        .path("/auth/refresh")
                        .build();

                // Remove tokens from response body
                response.setAccessToken(null);
                response.setRefreshToken(null);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                        .body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid refresh token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while refreshing token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody AuthenticationResponse tokens) {
        try {
            authenticationService.logout(tokens.getAccessToken(), tokens.getRefreshToken());
            return ResponseEntity.ok().body("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during logout");
        }
    }

}
