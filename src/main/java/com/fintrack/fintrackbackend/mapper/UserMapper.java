package com.fintrack.fintrackbackend.mapper;

import com.fintrack.fintrackbackend.dto.UserRequestDto;
import com.fintrack.fintrackbackend.dto.UserResponseDto;
import com.fintrack.fintrackbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "first_name", target = "firstName")
    @Mapping(source = "last_name", target = "lastName")
    @Mapping(source = "payday", target = "payday")
    User mapToEntity(UserRequestDto requestDto);

    @Mapping(source = "user.firstName", target = "first_name")
    @Mapping(source = "user.lastName", target = "last_name")
    @Mapping(source = "user.payday", target = "payday")
    @Mapping(source = "refreshToken", target = "refresh_token")
    UserResponseDto mapToDto(User user, String token, String refreshToken);
}