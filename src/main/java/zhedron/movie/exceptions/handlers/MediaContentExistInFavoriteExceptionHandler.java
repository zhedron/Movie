package zhedron.movie.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zhedron.movie.exceptions.MediaContentExistInFavoriteException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MediaContentExistInFavoriteExceptionHandler {
    @ExceptionHandler(MediaContentExistInFavoriteException.class)
    public ResponseEntity<Map<String, String>> handleMediaContentExistInFavoriteException(MediaContentExistInFavoriteException ex) {
        Map<String, String> response = new HashMap<>();

        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
