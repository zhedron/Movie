package zhedron.movie.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import zhedron.movie.dto.response.MediaContentResponse;
import zhedron.movie.entity.MediaContent;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = List.class, uses =  {ActorMapper.class})
public interface MediaContentMapper {
    @Mapping(
            target = "actors",
            expression = "java(mediaContent.getActors() == null ? null : mediaContent.getActors().stream().map(a -> String.format(\"{\\\"id\\\":%d,\\\"name\\\":\\\"%s\\\",\\\"surname\\\":\\\"%s\\\",\\\"year\\\":%d,\\\"age\\\":%d,\\\"photos\\\":%s,\\\"contentTypes\\\":%s,\\\"gender\\\":\\\"%s\\\"}\", a.getId(), a.getName(), a.getSurname(), a.getYear(), a.getAge(), a.getPhotos() == null ? \"[]\" : \"[\\\"\" + String.join(\"\\\",\\\"\", a.getPhotos()) + \"\\\"]\", a.getContentTypes() == null ? \"[]\" : \"[\\\"\" + String.join(\"\\\",\\\"\", a.getContentTypes()) + \"\\\"]\", a.getGender())).toList())"
    )
    MediaContentResponse toMediaContentResponse(MediaContent mediaContent);

    @Mapping(
            target = "actors",
            expression = "java(mediaContentResponse.actors() == null ? null : mediaContentResponse.actors().stream().map(s -> new zhedron.movie.entity.Actor()).toList())"
    )
    MediaContent toMediaContent(MediaContentResponse mediaContentResponse);

    List<MediaContentResponse> toMediaContentResponse(List<MediaContent> mediaContents);

/*    default String mapActorToString(Actor a) {
        if (a == null) return null;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("surname", a.getSurname());
            map.put("year", a.getYear());
            map.put("age", a.getAge());
            map.put("photos", a.getPhotos());
            map.put("contentTypes", a.getContentTypes());
            map.put("gender", a.getGender());

            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    default Actor mapStringToActor(String actorStr) {
        if (actorStr == null) return null;
        Actor actor = new Actor();
        actor.setName(actorStr);
        return actor;
    }*/
}
