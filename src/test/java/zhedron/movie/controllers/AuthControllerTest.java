package zhedron.movie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zhedron.movie.config.SecurityConfig;
import zhedron.movie.dto.response.request.LoginRequest;
import zhedron.movie.entity.User;
import zhedron.movie.enums.Role;
import zhedron.movie.repository.UserRepository;
import zhedron.movie.services.JwtService;
import zhedron.movie.services.RefreshTokenService;
import zhedron.movie.services.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, ControllerSecurityTestConfig.class})
class AuthControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    void loginIsPublicAndReturnsTokenAndHttpOnlyCookie() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("neo@example.com");
        request.setPassword("matrix");
        Authentication authentication = new TestingAuthenticationToken("neo@example.com", "matrix", "ADMIN");
        User user = new User();
        user.setEmail("neo@example.com");
        user.setRole(Role.USER);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userService.findByEmail("neo@example.com")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(refreshTokenService.generateRefreshToken("neo@example.com")).thenReturn("refresh-token");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().value("accessToken", "jwt-token"))
                .andExpect(cookie().httpOnly("refreshToken", true))
                .andExpect(cookie().value("refreshToken", "refresh-token"));
    }

    @Test
    void loginReturnsUnauthorizedForBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("neo@example.com");
        request.setPassword("wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
