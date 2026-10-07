package zhedron.movie.dto.response;

import java.util.List;

public record FavoriteResponse(long id, String name, List<MediaContentResponse> mediaContents, UserResponse user) {
}
