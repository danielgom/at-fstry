package com.encora.synth.aitooling.mapper;

import com.encora.synth.aitooling.dto.UserCreateRequest;
import com.encora.synth.aitooling.dto.UserGetResponse;
import com.encora.synth.aitooling.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(ignore = true, target = "id")
    User toUser(UserCreateRequest userCreationRequest);

    UserGetResponse toUserGetResponse(User user);
}
