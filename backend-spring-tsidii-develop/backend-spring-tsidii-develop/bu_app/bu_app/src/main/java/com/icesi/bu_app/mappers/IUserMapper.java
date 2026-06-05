package com.icesi.bu_app.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.icesi.bu_app.model.User;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    @Named("userToId")
    default Integer userToId(User user){
        return user!= null ? user.getCode() : null;
    }

    @Named("userToEmail")
    default String userToEmail(User user){
        return user!= null ? user.getEmail() : null;
    }
}
