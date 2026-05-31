package com.example.user_service_inno.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.user_service_inno.api.dto.PaymentCardDTO;
import com.example.user_service_inno.entity.PaymentCard;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentCardMapper {
    
    @Mapping(target = "userId", source = "user.id")
    PaymentCardDTO toDto(PaymentCard paymentCard);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user.id", ignore = true)
    PaymentCard toEntity(PaymentCardDTO paymentCardDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(PaymentCardDTO dto, @MappingTarget PaymentCard entity);
}
