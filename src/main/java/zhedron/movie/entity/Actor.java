package zhedron.movie.entity;

import jakarta.persistence.*;
import lombok.Data;
import zhedron.movie.enums.Gender;

import java.util.List;

@Entity
@Table(name = "actors")
@Data
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    private Integer year;

    private int age;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "actors")
    private List<MediaContent> mediaContents;

    private List<String> photos;

    private List<String> contentTypes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
}
