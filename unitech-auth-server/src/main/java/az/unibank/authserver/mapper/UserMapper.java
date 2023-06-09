package az.unibank.authserver.mapper;

import az.unibank.authserver.dto.request.RegisterNewUser;
import az.unibank.authserver.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User mapToEntity(RegisterNewUser registerNewUser);

}
