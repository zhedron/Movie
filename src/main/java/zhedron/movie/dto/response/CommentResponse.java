package zhedron.movie.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(long id, String text, UserResponse user, LocalDateTime createdAt, LocalDateTime updatedAt, boolean edited) {
}
