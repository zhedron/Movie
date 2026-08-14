package zhedron.movie.dto.response.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MediaContentRequest {
    @NotBlank(message = "Write title, title must not be empty")
    @NotNull(message = "Write title")
    private String title;

    @NotBlank(message = "Write description, description must not be empty")
    @NotNull(message = "Write description")
    private String description;

    private LocalDate releaseDate;

    private String trailerUrl;

    private String companyName;
}
