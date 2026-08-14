package zhedron.movie.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zhedron.movie.exceptions.MediaContentNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MediaContentNotFoundExceptionHandler {
    @ExceptionHandler(MediaContentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleMediaContentNotFoundException(MediaContentNotFoundException e) {
        Map<String, String> response = new HashMap<>();

        response.put("message", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
