package zhedron.movie.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import zhedron.movie.exceptions.CommentDeleteAccessDeniedException;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CommentDeleteAccessDeniedExceptionHandler {
    @ExceptionHandler(CommentDeleteAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleCommentDeleteAccessDeniedException(CommentDeleteAccessDeniedException e) {
        Map<String, String> response = new HashMap<>();

        response.put("message", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
