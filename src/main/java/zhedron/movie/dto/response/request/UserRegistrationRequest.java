package zhedron.movie.dto.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    @NotNull(message = "Write your username")
    @NotBlank(message = "User must not be empty")
    private String username;

    @NotNull(message = "Write your email")
    @NotBlank(message = "Email must not be empty")
    private String email;

    @NotBlank(message = "Write your password")
    private String password;
}
