package zhedron.movie.dto.response;

import zhedron.movie.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(long id, String username, String email, LocalDateTime createdAt, LocalDateTime updatedAt, Role role) {
}
