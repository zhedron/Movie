package zhedron.movie.entity;

import jakarta.persistence.*;
import lombok.Data;
import zhedron.movie.enums.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "media_contents")
@Data
public class MediaContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 2048)
    private String title;

    @Column(nullable = false)
    private String description;

    private LocalDate releaseDate;

    private long duration;

    private List<String> coverArts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "film_id")
    private Film film;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "media_content_id")
    private List<Season> seasons;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String trailerUrl;

    private String companyName;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "media_content_id")
    private List<Comment> comments = new ArrayList<>();
}
