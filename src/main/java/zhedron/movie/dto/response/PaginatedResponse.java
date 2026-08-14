package zhedron.movie.dto.response;

import java.util.List;


public record PaginatedResponse(List<MediaContentResponse> mediaContentResponses, int page, int size,
                                boolean hasNext, boolean hasPrevious, long totalElements,
                                int totalPages, boolean first, boolean last) {
}
