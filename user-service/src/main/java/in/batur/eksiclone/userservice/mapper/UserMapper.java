package in.batur.eksiclone.userservice.mapper;

import in.batur.eksiclone.entity.user.Role;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.userservice.dto.UserDTO;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .isActive(user.isActive())
                .lastLoginDate(user.getLastLoginDate())
                .roles(user.getRoles() != null ? 
                       user.getRoles().stream()
                           .map(Role::getName)
                           .collect(Collectors.toSet()) : 
                       null)
                .createdDate(user.getCreatedDate())
                .lastUpdatedDate(user.getLastUpdatedDate())
                .build();
    }
}