package zhedron.movie.exceptions;

public class CommentDeleteAccessDeniedException extends RuntimeException {
    public CommentDeleteAccessDeniedException(String message) {
        super(message);
    }
}
