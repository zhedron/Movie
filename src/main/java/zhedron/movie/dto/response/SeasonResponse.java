package zhedron.movie.dto.response;

import java.util.List;

public record SeasonResponse(long id, List<EpisodeResponse> episodes, int seasonNumber) {
}
