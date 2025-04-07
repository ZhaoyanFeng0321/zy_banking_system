package zycode.web.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import zycode.web.app.service.JwtService;
import zycode.web.app.service.TokenBlacklistService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TokenBlacklistIntegrationTest {

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testBlacklistingToken() {
        // Generate a test token
        String username = "testuser";
        String token = jwtService.generateAccessToken(username);

        // Blacklist it
        blacklistService.blacklistToken(token);

        // Check if it's blacklisted
        assertTrue(blacklistService.isBlacklisted(token));

        // Verify directly in Redis
        String key = "token:blacklist:" + token;
        Boolean exists = redisTemplate.hasKey(key);
        assertTrue(exists);
    }

    @Test
    public void testTokenExpirationInBlacklist() throws InterruptedException {
        // Create token with very short expiration (e.g., 2 seconds)
        // You'll need a special method for this test
        String shortLivedToken = jwtService.generateTokenWithCustomExpiration("testuser", 2000);

        // Blacklist it
        blacklistService.blacklistToken(shortLivedToken);

        // Verify it's in the blacklist
        assertTrue(blacklistService.isBlacklisted(shortLivedToken));

        // Wait for expiration (add a little buffer)
        Thread.sleep(3000);

        // Check it's removed from blacklist
        assertFalse(blacklistService.isBlacklisted(shortLivedToken));
    }

    @Test
    public void testRedisConnection() {
        redisTemplate.opsForValue().set("test", "value");
        assertEquals("value", redisTemplate.opsForValue().get("test"));
    }
}
