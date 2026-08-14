package zhedron.movie.services.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import zhedron.movie.dto.response.UserResponse;
import zhedron.movie.dto.response.request.UserRegistrationRequest;
import zhedron.movie.entity.User;
import zhedron.movie.enums.Role;
import zhedron.movie.exceptions.UserExistException;
import zhedron.movie.exceptions.UserNotFoundException;
import zhedron.movie.mappers.UserMapper;
import zhedron.movie.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUserEncodesPasswordAndSavesAdminUser() {
        UserRegistrationRequest request = registrationRequest("neo", "neo@example.com", "matrix");
        User savedUser = new User();
        savedUser.setId(42L);
        savedUser.setUsername("neo");
        savedUser.setEmail("neo@example.com");
        savedUser.setRole(Role.ADMIN);
        UserResponse expectedResponse = new UserResponse(42L, "neo", "neo@example.com", savedUser.getCreatedAt(), null, Role.ADMIN);
        when(userRepository.existsByEmail("neo@example.com")).thenReturn(false);
        when(passwordEncoder.encode("matrix")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserDTO(savedUser)).thenReturn(expectedResponse);

        UserResponse response = userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User userToSave = userCaptor.getValue();
        assertEquals("neo", userToSave.getUsername());
        assertEquals("neo@example.com", userToSave.getEmail());
        assertEquals("encoded-password", userToSave.getPassword());
        assertEquals(Role.ADMIN, userToSave.getRole());
        assertNotNull(userToSave.getCreatedAt());
        assertSame(expectedResponse, response);
    }

    @Test
    void createUserThrowsWhenEmailAlreadyExists() {
        UserRegistrationRequest request = registrationRequest("neo", "neo@example.com", "matrix");
        when(userRepository.existsByEmail("neo@example.com")).thenReturn(true);

        assertThrows(UserExistException.class, () -> userService.createUser(request));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void findByEmailThrowsWhenUserIsMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByEmail("missing@example.com"));
    }

    @Test
    void getCurrentUserLooksUpAuthenticatedEmail() {
        User user = new User();
        user.setEmail("current@example.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("current@example.com", "password")
        );
        when(userRepository.findByEmail("current@example.com")).thenReturn(Optional.of(user));

        assertSame(user, userService.getCurrentUser());
    }

    @Test
    void changeRoleIsOk() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        UserResponse userResponse = new UserResponse(user.getId(), null, null, null, null, Role.ADMIN);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@test.com", "password"
                )
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userResponse);

        UserResponse response = userService.changeRole(user.getId(), Role.ADMIN);

        assertEquals(Role.ADMIN, response.role());
    }

    private static UserRegistrationRequest registrationRequest(String username, String email, String password) {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}
