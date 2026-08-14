package zhedron.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhedron.movie.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
