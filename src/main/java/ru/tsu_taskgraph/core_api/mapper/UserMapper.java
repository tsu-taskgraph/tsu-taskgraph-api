package ru.tsu_taskgraph.core_api.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tsu_taskgraph.core_api.dto.user.UserProfile;
import ru.tsu_taskgraph.core_api.entity.User;
@Mapper(componentModel = "spring")
public abstract class UserMapper {
    public abstract UserProfile toUserProfile(User user);
}