package org.example.Mapper;

import org.example.DTO.UserDTO;
import org.example.model.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User entity);

    User toEntity(UserDTO dto);

}
