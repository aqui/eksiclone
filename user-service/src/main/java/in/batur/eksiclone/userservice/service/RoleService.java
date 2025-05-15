package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.userservice.dto.CreateRoleRequest;
import in.batur.eksiclone.userservice.dto.RoleDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    Page<RoleDTO> getAllRoles(Pageable pageable);
    
    RoleDTO getRoleById(Long id);
    
    RoleDTO createRole(CreateRoleRequest request);
    
    RoleDTO updateRole(Long id, CreateRoleRequest request);
    
    void deleteRole(Long id);
}