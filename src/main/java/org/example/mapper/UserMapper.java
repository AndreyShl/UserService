package org.example.mapper;

import org.example.dto.Userdto;
import org.example.model.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    Userdto toDTO(User entity);

    User toEntity(Userdto dto);

}
