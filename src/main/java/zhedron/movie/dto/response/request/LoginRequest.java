package zhedron.movie.dto.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Write your email, email must not be empty")
    @NotNull(message = "Write your email")
    private String email;
    @NotBlank(message = "Write your password, password must not be empty")
    @NotNull(message = "Write your password")
    private String password;
}
