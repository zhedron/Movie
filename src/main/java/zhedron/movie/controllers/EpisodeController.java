package zhedron.movie.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhedron.movie.dto.response.EpisodeResponse;
import zhedron.movie.dto.response.request.EpisodeCreateRequest;
import zhedron.movie.services.EpisodeService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/episode")
@Tag(name = "Episodes", description = "Endpoints for uploading, streaming, and managing video episodes")
public class EpisodeController {
    private final EpisodeService episodeService;

    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload a video episode",
            description = "Uploads an MP4 video file along with episode metadata using multipart form data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Episode uploaded successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EpisodeResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid upload request (missing video, empty file, or non-MP4 format)",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = {
                                    @ExampleObject(name = "Missing Video", value = "Video is required"),
                                    @ExampleObject(name = "Empty File", value = "Upload video file"),
                                    @ExampleObject(name = "Invalid Format", value = "Upload video file with mp4 format")
                            }
                    )
            )
    })
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile video, @RequestPart @Valid EpisodeCreateRequest episodeCreateRequest) throws IOException {
        if (video == null) {
            return ResponseEntity.badRequest().body("Video is required");
        } else if (video.isEmpty()) {
            return ResponseEntity.badRequest().body("Upload video file");
        } else if (!video.getContentType().equals("video/mp4")) {
            return ResponseEntity.badRequest().body("Upload video file with mp4 format");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(episodeService.uploadEpisode(video, episodeCreateRequest));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete episode by ID",
            description = "Deletes the episode record from the database and removes its corresponding video file from storage."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Episode deleted successfully",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(name = "Success", value = "Episode deleted successfully")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Episode not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Not Found", value = "{\"message\": \"Episode not found with 1\"}")
                    )
            )
    })
    public ResponseEntity<String> deleteEpisodeById(@PathVariable long id) {
        episodeService.deleteById(id);

        return ResponseEntity.ok("Episode deleted successfully");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Stream video episode",
            description = "Retrieves and streams the video file resource associated with the specified episode ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Video file stream",
                    content = @Content(
                            mediaType = "video/mp4",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed file URI or loading error",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(name = "Load Error", value = "Failed to load file: Invalid path")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Episode not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Not Found", value = "{\"message\": \"Episode not found with 1\"}")
                    )
            )
    })
    public ResponseEntity<?> streamFile(@PathVariable long id) {
        EpisodeResponse episodeResponse = episodeService.findById(id);

        try {
            Path path = Paths.get("episode/").resolve(episodeResponse.episodeUrl()).normalize();

            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.parseMediaType(episodeResponse.contentType())).body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Failed to load file: " + e.getMessage());
        }
    }
}
