package in.batur.eksiclone.userservice.mapper;

import in.batur.eksiclone.entity.user.Role;
import in.batur.eksiclone.userservice.dto.RoleDTO;

import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleDTO toDto(Role role) {
        if (role == null) {
            return null;
        }
        
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdDate(role.getCreatedDate())
                .lastUpdatedDate(role.getLastUpdatedDate())
                .build();
    }
}