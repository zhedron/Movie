package zhedron.movie.services;

import zhedron.movie.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    String generateRefreshToken(String email);

    RefreshToken validateToken(RefreshToken refreshToken);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
