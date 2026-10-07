package zhedron.movie.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhedron.movie.dto.response.ActorResponse;
import zhedron.movie.dto.response.MessageResponse;
import zhedron.movie.dto.response.request.ActorRequest;
import zhedron.movie.entity.Actor;
import zhedron.movie.mappers.ActorMapper;
import zhedron.movie.services.ActorService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/actor")
@Tag(name = "Actors", description = "Endpoints for creating, updating, retrieving, deleting, and streaming actor photos")
public class ActorController {
    private final ActorService actorService;

    private final ActorMapper actorMapper;

    public ActorController(ActorService actorService, ActorMapper actorMapper) {
        this.actorService = actorService;
        this.actorMapper = actorMapper;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    encoding = @Encoding(name = "actorRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    @Operation(
            summary = "Create actor",
            description = "Creates a new actor profile and uploads one or more JPEG/PNG photos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Actor created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ActorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Image is empty or has unsupported content type",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = {
                                    @ExampleObject(name = "Empty Image", value = "{\"message\": \"Upload image file\"}"),
                                    @ExampleObject(name = "Invalid Image Type", value = "{\"message\": \"Invalid image type\"}")
                            }
                    )
            )
    })
    public ResponseEntity<?> createActor(@RequestPart ActorRequest actorRequest, @RequestPart List<MultipartFile> images) throws IOException {
        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Upload image file"));
            } else if (!image.getContentType().equals("image/jpeg") && !image.getContentType().equals("image/png")) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid image type"));
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(actorRequest, images));
    }

    @GetMapping("/{actorId}")
    @Operation(
            summary = "Get actor by ID",
            description = "Retrieves actor details by unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actor found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ActorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Actor not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Not Found", value = "{\"message\": \"Actor not found with 1\"}")
                    )
            )
    })
    public ResponseEntity<ActorResponse> findActorById(@PathVariable long actorId) {
        Actor actor = actorService.findById(actorId);

        return ResponseEntity.ok(actorMapper.toActorResponse(actor));
    }

    @GetMapping("/stream/{actorId}")
    @Operation(
            summary = "Stream actor photo",
            description = "Streams one actor photo by zero-based photo index."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Photo streamed successfully",
                    content = {
                            @Content(mediaType = MediaType.IMAGE_JPEG_VALUE),
                            @Content(mediaType = MediaType.IMAGE_PNG_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file URL or photo index",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = {
                                    @ExampleObject(name = "Invalid Url", value = "{\"message\": \"Invalid url\"}"),
                                    @ExampleObject(name = "Photo Index Too Large", value = "{\"message\": \"Count of size more than actor has photos\"}")
                            }
                    )
            )
    })
    public ResponseEntity<?> streamPhotos(@PathVariable long actorId, @RequestParam int size) {
        try {
            Actor actor = actorService.findById(actorId);

            String photoURL = actor.getPhotos().get(size - 1);

            String contentType = actor.getContentTypes().get(size - 1);

            Path path = Paths.get("images/").resolve(photoURL).normalize();

            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid url"));
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Count of size more than actor has photos"));
        }
    }

    @DeleteMapping("/delete/{actorId}")
    @Operation(
            summary = "Delete actor by ID",
            description = "Deletes an actor and removes the actor from linked media content."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actor deleted successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(name = "Success", value = "{\"message\": \"Successfully deleted actor\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Actor not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Not Found", value = "{\"message\": \"Actor not found with 1\"}")
                    )
            )
    })
    public ResponseEntity<MessageResponse> deleteActorById(@PathVariable long actorId) {
        actorService.deleteById(actorId);

        return ResponseEntity.ok().body(new MessageResponse("Successfully deleted actor"));
    }

    @PatchMapping("/update/{actorId}")
    @Operation(
            summary = "Update actor by ID",
            description = "Updates actor profile fields. Null request fields are ignored."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actor updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ActorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Actor not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "Not Found", value = "{\"message\": \"Actor not found with 1\"}")
                    )
            )
    })
    public ResponseEntity<ActorResponse> updateActorById(@PathVariable long actorId, @RequestBody ActorRequest actorRequest) {
        return ResponseEntity.ok(actorService.updateActor(actorRequest, actorId));
    }
}
