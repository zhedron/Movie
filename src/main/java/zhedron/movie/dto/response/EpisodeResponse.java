package zhedron.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record EpisodeResponse(long id, String episodeUrl, long episodeNumber, String contentType) {
}
