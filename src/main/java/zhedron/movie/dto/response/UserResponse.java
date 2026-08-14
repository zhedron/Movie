package zhedron.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import zhedron.movie.enums.Role;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record UserResponse(long id, String username, String email, LocalDateTime createdAt, LocalDateTime updatedAt, Role role) {
}
