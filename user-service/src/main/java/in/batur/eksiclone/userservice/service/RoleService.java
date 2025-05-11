package in.batur.eksiclone.userservice.service;

import java.util.List;

import in.batur.eksiclone.userservice.dto.RoleDTO;

public interface RoleService {
    List<RoleDTO> findAll();
    RoleDTO findById(Long id);
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO updateRole(RoleDTO roleDTO);
    void deleteRole(Long id);
}