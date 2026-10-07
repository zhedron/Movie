package zhedron.movie.exceptions;

public class MediaContentNotExistInFavoriteException extends RuntimeException {
    public MediaContentNotExistInFavoriteException(String message) {
        super(message);
    }
}
