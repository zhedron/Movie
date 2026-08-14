package zhedron.movie.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import zhedron.movie.dto.response.MediaContentResponse;
import zhedron.movie.entity.MediaContent;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MediaContentMapper {
    MediaContentResponse toMediaContentResponse(MediaContent mediaContent);

    MediaContent toMediaContent(MediaContentResponse mediaContentResponse);

    List<MediaContentResponse> toMediaContentResponse(List<MediaContent> mediaContents);

    List<MediaContent> toMediaContent(List<MediaContentResponse> mediaContents);
}
