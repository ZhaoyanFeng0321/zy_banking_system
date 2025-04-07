package zycode.web.app.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import zycode.web.app.service.JwtService;
import zycode.web.app.entity.User;
import zycode.web.app.service.TokenBlacklistService;

import java.io.IOException;

/**
 * A filter that authenticates every incoming requests using JWT tokens.
 * This filter is responsible for extracting the JWT token from cookies,
 * validating it, and setting up the user's authentication context.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Look for the token in cookies
        String jwtToken = extractTokenFromCookies(request);

        // If no token found, continue filter chain
        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if token is valid and not blacklisted
        if (!jwtService.isTokenValid(jwtToken) || blacklistService.isBlacklisted(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String subject = jwtService.extractSubject(jwtToken);
        User user = (User) userDetailsService.loadUserByUsername(subject);
        SecurityContext context = SecurityContextHolder.getContext();

        if (user != null && context.getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
            authenticationToken.setDetails(request);
            context.setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from cookies
     */
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}