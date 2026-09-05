package zhedron.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhedron.movie.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
