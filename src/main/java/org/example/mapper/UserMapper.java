package org.example.mapper;

import org.example.dto.Userdto;
import org.example.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {

    Userdto toDTO(User entity);

    User toEntity(Userdto dto);

    void updateUserFromDto(Userdto dto, @MappingTarget User entity);
}
