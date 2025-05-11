package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.entity.User;
import in.batur.eksiclone.userservice.dto.UserDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToRoleNames")
    UserDTO toDto(User user);
    
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    User toEntity(UserDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    void updateEntity(UserDTO dto, @MappingTarget User user);
    
    @Named("rolesToRoleNames")
    default List<String> rolesToRoleNames(List<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
}