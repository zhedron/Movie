package zhedron.movie.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import zhedron.movie.dto.response.UserResponse;
import zhedron.movie.entity.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponse toUserDTO(User user);

    User toUser(UserResponse userResponse);

    List<UserResponse> toUserDTO(List<User> users);

    List<User> toUser(List<UserResponse> users);
}
