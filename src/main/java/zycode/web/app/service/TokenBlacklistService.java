package zycode.web.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * Adds a token to the blacklist in Redis
     */
    public void blacklistToken(String token) {
        try {
            // Calculate remaining time (time-to-live) until token expiration
            Date expiration = jwtService.extractExpiration(token);
            long ttl = expiration.getTime() - System.currentTimeMillis();

            /*
            * Only blacklist if token isn't already expired, avoid unnecessarily storing expired tokens
            * entry in Redis should expire when the refresh token expires.
            * Once the TTL is reached, Redis automatically removes the entry from memory, ensuring the blacklist doesn't grow indefinitely.
            */
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + token,
                        "revoked",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            // Log the error
            System.err.println("Failed to blacklist token: " + e.getMessage());
        }
    }

    /**
     * Checks if a token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        Boolean exists = redisTemplate.hasKey(BLACKLIST_PREFIX + token);
        return exists != null && exists;
    }
}


