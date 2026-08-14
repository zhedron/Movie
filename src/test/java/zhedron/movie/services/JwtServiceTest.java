package zhedron.movie.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import zhedron.movie.entity.User;
import zhedron.movie.enums.Role;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
    private static final String TEST_KEY = "16621e52f07d4d20aab700118e1a9f6672f9dcf73b085a0fb2dff7efd3acd634aa45ab1a153569d24d28f83c2493eeb53360694ca9733ad1c4010e17df28fca2";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "key", TEST_KEY);
    }

    @Test
    void generateTokenIncludesUserIdentityAndRole() {
        User user = new User();
        user.setId(7L);
        user.setEmail("admin@example.com");
        user.setRole(Role.ADMIN);

        String token = jwtService.generateToken(user);

        assertEquals("admin@example.com", jwtService.extractEmail(token));
        assertEquals("ADMIN", jwtService.getAllClaims(token).get("roles"));
        assertEquals(7, jwtService.getAllClaims(token).get("id"));
    }

    @Test
    void validateTokenReturnsTrueForMatchingUserDetails() {
        User user = new User();
        user.setId(11L);
        user.setEmail("viewer@example.com");
        user.setRole(Role.USER);
        UserDetails details = new org.springframework.security.core.userdetails.User(
                "viewer@example.com",
                "password",
                Collections.emptyList()
        );

        assertTrue(jwtService.validateToken(jwtService.generateToken(user), details));
    }
}
