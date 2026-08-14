package zhedron.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record PaginatedResponse(List<MediaContentResponse> mediaContentResponses, int page, int size,
                                boolean hasNext, boolean hasPrevious, long totalElements,
                                int totalPages, boolean first, boolean last) {
}
