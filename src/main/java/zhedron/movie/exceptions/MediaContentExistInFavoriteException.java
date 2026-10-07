package zhedron.movie.exceptions;

public class MediaContentExistInFavoriteException extends RuntimeException {
    public MediaContentExistInFavoriteException(String message) {
        super(message);
    }
}
