package com.example.app.mapper;

import com.example.app.dto.UserDto;
import com.example.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);


    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    List<UserDto> usersToUserDtos(List<User> users);

    List<User> userDtosToUsers(List<UserDto> userDtos);
}
