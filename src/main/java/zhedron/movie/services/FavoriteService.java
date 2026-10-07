package zhedron.movie.services;

import zhedron.movie.dto.response.FavoriteResponse;
import zhedron.movie.dto.response.request.FavoriteRequest;
import zhedron.movie.entity.Favorite;

public interface FavoriteService {
    FavoriteResponse create(FavoriteRequest favoriteRequest);

    FavoriteResponse update(FavoriteRequest favoriteRequest, long id);

    void deleteFavorite(long id);

    Favorite findById(long id);

    FavoriteResponse addMediaContentToFavorite(long favoriteId, long mediaId);

    FavoriteResponse removeMediaContentFromFavorite(long favoriteId, long mediaId);

    FavoriteResponse getFavoriteByName(String name);
}
