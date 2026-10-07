package zhedron.movie.exceptions;

public class FavoriteExistException extends RuntimeException {
    public FavoriteExistException(String message) {
        super(message);
    }
}
