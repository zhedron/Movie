package zhedron.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhedron.movie.entity.Actor;

public interface ActorRepository extends JpaRepository<Actor, Long> {
}
