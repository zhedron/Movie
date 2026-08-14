package zhedron.movie.exceptions;

public class MediaContentNotFoundException extends RuntimeException {
    public MediaContentNotFoundException(String message) {
        super(message);
    }
}
