package zhedron.movie.dto.response.request;

import lombok.Data;
import zhedron.movie.enums.Gender;

@Data
public class ActorRequest {
    private String name;

    private String surname;

    private Integer year;

    private Gender gender;
}
