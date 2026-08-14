package zhedron.movie.dto.response;

import zhedron.movie.enums.Status;

import java.time.LocalDate;
import java.util.List;


public record MediaContentResponse(long id, String title, String description,
                                   LocalDate releaseDate, long duration, List<String> coverArts,
                                   FilmResponse film, List<SeasonResponse> seasons, Status status,
                                   String trailerUrl, String companyName, List<CommentResponse> comments) {
}
