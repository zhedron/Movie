package zhedron.movie.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import zhedron.movie.dto.response.ActorResponse;
import zhedron.movie.entity.Actor;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {MediaContentMapper.class})
public interface ActorMapper {
    ActorResponse toActorResponse(Actor actor);

    Actor toActor(ActorResponse actorResponse);

    List<ActorResponse> toActorResponseList(List<Actor> actors);

    List<Actor> toActorList(List<ActorResponse> actorResponseList);
}
