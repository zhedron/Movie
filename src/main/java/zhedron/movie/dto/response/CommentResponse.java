package zhedron.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record CommentResponse(long id, String text, UserResponse user, LocalDateTime createdAt, LocalDateTime updatedAt, boolean edited) {
}
