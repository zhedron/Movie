package zhedron.movie.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "films")
@Data
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String videoUrl;

    private String contentType;
}
