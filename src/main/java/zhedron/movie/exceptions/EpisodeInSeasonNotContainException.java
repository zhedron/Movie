package zhedron.movie.exceptions;

public class EpisodeInSeasonNotContainException extends RuntimeException {
    public EpisodeInSeasonNotContainException(String message) {
        super(message);
    }
}
