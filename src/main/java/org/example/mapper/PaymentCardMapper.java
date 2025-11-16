package org.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.PaymentCarddto;
import org.example.model.entity.PaymentCard;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCarddto toDTO(PaymentCard entity);

    @Mapping(source = "userId", target = "user.id")
    PaymentCard toEntity(PaymentCarddto dto);
    void updateCardFromDTO(PaymentCarddto dto, @MappingTarget PaymentCard entity);
}