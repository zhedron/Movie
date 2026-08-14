package zhedron.movie.services;

import zhedron.movie.dto.response.CommentResponse;
import zhedron.movie.dto.response.request.CommentRequest;

public interface CommentService {
    CommentResponse addComment(CommentRequest commentRequest, long mediaContentId);

    CommentResponse findById(long commentId, long mediaContentId);

    void deleteComment(long id);

    CommentResponse editComment(CommentRequest commentRequest, long id);
}
