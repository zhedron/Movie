package zhedron.movie.dto.response;

import zhedron.movie.entity.Episode;

import java.util.List;

public record SeasonResponse(long id, List<Episode> episodes, int seasonNumber) {
}
