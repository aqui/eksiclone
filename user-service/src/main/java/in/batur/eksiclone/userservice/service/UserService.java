package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.userservice.dto.CreateUserRequest;
import in.batur.eksiclone.userservice.dto.UpdateUserRequest;
import in.batur.eksiclone.userservice.dto.UserDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDTO> getAllUsers(Pageable pageable);
    
    UserDTO getUserById(Long id);
    
    UserDTO getUserByUsername(String username);
    
    UserDTO getUserByEmail(String email);
    
    UserDTO createUser(CreateUserRequest request);
    
    UserDTO updateUser(Long id, UpdateUserRequest request);
    
    void deleteUser(Long id);
    
    UserDTO assignRoleToUser(Long userId, Long roleId);
    
    UserDTO removeRoleFromUser(Long userId, Long roleId);
    
    Page<UserDTO> searchUsers(String query, Pageable pageable);
    
    Page<UserDTO> searchUsersByEmail(String email, Pageable pageable);
    
    Page<UserDTO> getUsersByRoleName(String roleName, Pageable pageable);
    
    Page<UserDTO> getUsersByRoleId(Long roleId, Pageable pageable);
}