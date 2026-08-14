package zhedron.movie.services;

import zhedron.movie.dto.response.UserResponse;
import zhedron.movie.dto.response.request.UserRegistrationRequest;
import zhedron.movie.entity.User;
import zhedron.movie.enums.Role;

public interface UserService {
    UserResponse createUser(UserRegistrationRequest userRegistrationRequest);

    User findByEmail(String email);

    User getCurrentUser();

    UserResponse changeRole(long userId, Role role);
}
