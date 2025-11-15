package org.example.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.DTO.PaymentCardDTO;
import org.example.model.entity.PaymentCard;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDTO toDTO(PaymentCard entity);

    @Mapping(source = "userId", target = "user.id")
    PaymentCard toEntity(PaymentCardDTO dto);
}