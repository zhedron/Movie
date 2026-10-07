package zhedron.movie.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zhedron.movie.exceptions.FavoriteExistException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class FavoriteExistExceptionHandler {
    @ExceptionHandler(FavoriteExistException.class)
    public ResponseEntity<Map<String, String>> handleFavoriteExistException(FavoriteExistException e) {
        Map<String, String> response = new HashMap<>();

        response.put("message", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
