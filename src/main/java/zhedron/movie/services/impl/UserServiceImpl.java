package zhedron.movie.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zhedron.movie.dto.response.UserResponse;
import zhedron.movie.dto.response.request.UserRegistrationRequest;
import zhedron.movie.entity.User;
import zhedron.movie.enums.Role;
import zhedron.movie.exceptions.UserExistException;
import zhedron.movie.exceptions.UserNotFoundException;
import zhedron.movie.mappers.UserMapper;
import zhedron.movie.repository.UserRepository;
import zhedron.movie.services.UserService;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse createUser(UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.existsByEmail(userRegistrationRequest.getEmail())) {
            throw new UserExistException("Email already exists");
        }

        User user = new User();

        user.setEmail(userRegistrationRequest.getEmail());
        user.setUsername(userRegistrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRole(Role.ADMIN);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with " + email));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return findByEmail(authentication.getName());
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserResponse changeRole(long userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with " + userId));

        user.setRole(role);

        User savedUser = userRepository.save(user);

        return userMapper.toUserDTO(savedUser);
    }
}
