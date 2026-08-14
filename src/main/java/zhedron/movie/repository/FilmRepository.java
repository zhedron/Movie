package zhedron.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhedron.movie.entity.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {
}
