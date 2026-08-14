package zhedron.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhedron.movie.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
}
