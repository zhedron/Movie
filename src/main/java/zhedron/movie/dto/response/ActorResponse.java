package zhedron.movie.dto.response;

import zhedron.movie.enums.Gender;

import java.util.List;

public record ActorResponse(long id, String name, String surname, Integer year,
                            int age, List<MediaContentResponse> mediaContents, List<String> photos,
                            List<String> contentTypes, Gender gender) {
}
