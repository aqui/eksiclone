package in.batur.eksiclone.roleservice.service;

import java.util.List;

public interface RoleService {
    List<RoleDTO> findAll();
    RoleDTO findById(Long id);
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO updateRole(RoleDTO roleDTO);
    void deleteRole(Long id);
}