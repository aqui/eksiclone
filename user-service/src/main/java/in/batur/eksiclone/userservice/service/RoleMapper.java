package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.userservice.dto.RoleDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    RoleDTO toDto(Role role);
    
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    Role toEntity(RoleDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    void updateEntity(RoleDTO dto, @MappingTarget Role role);
}