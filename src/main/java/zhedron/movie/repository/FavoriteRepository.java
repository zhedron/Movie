package zhedron.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zhedron.movie.entity.Favorite;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f JOIN f.mediaContents m WHERE m.id = :mediaContentId")
    Favorite findByMediaContentId(long mediaContentId);

    Optional<Favorite> findByUserIdAndName(long userId, String name);

    boolean existsByUserIdAndName(long userId, String name);
}
