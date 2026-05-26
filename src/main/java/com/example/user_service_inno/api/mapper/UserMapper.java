package com.example.user_service_inno.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.user_service_inno.api.dto.UserDTO;
import com.example.user_service_inno.entity.User;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PaymentCardMapper.class,  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserDTO dto, @MappingTarget User entity);
}
