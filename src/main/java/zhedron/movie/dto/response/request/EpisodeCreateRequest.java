package zhedron.movie.dto.response.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EpisodeCreateRequest {
    @Min(value = 1, message = "Enter a number")
    private long episodeNumber;
}
