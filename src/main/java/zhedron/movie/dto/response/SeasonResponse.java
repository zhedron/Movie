package zhedron.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import zhedron.movie.entity.Episode;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record SeasonResponse(long id, List<Episode> episodes, int seasonNumber) {
}
