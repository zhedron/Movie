package zhedron.movie.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhedron.movie.dto.response.request.LoginRequest;
import zhedron.movie.entity.User;
import zhedron.movie.services.JwtService;
import zhedron.movie.services.UserService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication", description = "Endpoints for user authentication and JWT session management")
public class AuthController {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtService jwtService, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticates user credentials. On success, returns a JWT token in the response body and sets an HttpOnly 'accessToken' cookie."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    headers = @Header(
                            name = HttpHeaders.SET_COOKIE,
                            description = "HttpOnly cookie containing the JWT token",
                            schema = @Schema(type = "string", example = "accessToken=eyJhbGci...; Path=/; Max-Age=3600; HttpOnly")
                    ),
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Successful Response",
                                    value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error in request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Email Error",
                                            summary = "When email field validation fails",
                                            value = "{\"error\": \"Write your email, email must not be empty\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Missing Password Error",
                                            summary = "When password field validation fails",
                                            value = "{\"error\": \"Write your password, password must not be empty\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials provided",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Bad Credentials",
                                    value = "Invalid email or password"
                            )
                    )
            )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                Map<String, Object> errors = new HashMap<>();

                errors.put("error", error.getDefaultMessage());

                return ResponseEntity.badRequest().body(errors);
            }
        }

            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                ));

                if (authentication.isAuthenticated()) {
                    User user = userService.findByEmail(loginRequest.getEmail());

                    String token = jwtService.generateToken(user);

                    ResponseCookie cookie = ResponseCookie.from("accessToken")
                            .httpOnly(true)
                            .maxAge(Duration.ofHours(1))
                            .path("/")
                            .value(token)
                            .build();

                    Map<String, String> response = new HashMap<>();

                    response.put("token", token);

                    return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
                }
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            }
        return null;
    }
}