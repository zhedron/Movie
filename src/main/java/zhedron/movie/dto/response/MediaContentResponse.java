package zhedron.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import zhedron.movie.enums.Status;

import java.time.LocalDate;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record MediaContentResponse(long id, String title, String description,
                                   LocalDate releaseDate, long duration, List<String> coverArts,
                                   FilmResponse film, List<SeasonResponse> seasons, Status status,
                                   String trailerUrl, String companyName, List<CommentResponse> comments) {
}
