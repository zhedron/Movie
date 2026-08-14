package zhedron.movie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import zhedron.movie.config.SecurityConfig;
import zhedron.movie.dto.response.UserResponse;
import zhedron.movie.dto.response.request.UserRegistrationRequest;
import zhedron.movie.enums.Role;
import zhedron.movie.repository.UserRepository;
import zhedron.movie.services.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, ControllerSecurityTestConfig.class})
class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    void registrationIsPublicAndReturnsCreatedUser() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("neo");
        request.setEmail("neo@example.com");
        request.setPassword("matrix");
        when(userService.createUser(any(UserRegistrationRequest.class)))
                .thenReturn(new UserResponse(1L, "neo", "neo@example.com", null, null, Role.ADMIN));

        mockMvc.perform(post("/api/user/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("neo"))
                .andExpect(jsonPath("$.email").value("neo@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService).createUser(any(UserRegistrationRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void changeRoleIfUserAdminIsSuccessful() throws Exception {
        when(userService.changeRole(1L, Role.ADMIN)).thenReturn(new UserResponse(1L, null, null, null, null, Role.ADMIN));

        mockMvc.perform(put("/api/user/change-role/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ADMIN\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "USER")
    public void changeRoleIfUserUserIsSuccessful() throws Exception {
        mockMvc.perform(put("/api/user/change-role/1"))
                .andExpect(status().isForbidden());
    }
}
