package zhedron.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhedron.movie.entity.Season;

public interface SeasonRepository extends JpaRepository<Season, Long> {
}
