package zhedron.movie.dto.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Write your comment, text must not be empty")
    @NotNull(message = "Write your comment")
    private String text;
}
