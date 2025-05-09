package in.batur.eksiclone.roleservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.repository.RoleRepository;
import in.batur.eksiclone.roleservice.exception.RoleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<RoleDTO> findAll() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = roles.stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setRoleName(role.getRoleName());
            return dto;
        }).collect(Collectors.toList());
        log.info("Found {} roles", roleDTOs.size());
        return roleDTOs;
    }

    @Override
    public RoleDTO findById(Long id) {
        log.info("Fetching role with id {}", id);
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        return dto;
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        log.info("Creating role with name {}", roleDTO.getRoleName());
        if (roleRepository.findByRoleName(roleDTO.getRoleName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists: " + roleDTO.getRoleName());
        }
        Role role = new Role();
        role.setRoleName(roleDTO.getRoleName());
        Role savedRole = roleRepository.save(role);
        RoleDTO responseDTO = new RoleDTO();
        responseDTO.setId(savedRole.getId());
        responseDTO.setRoleName(savedRole.getRoleName());
        return responseDTO;
    }

    @Override
    public RoleDTO updateRole(RoleDTO roleDTO) {
        log.info("Updating role with id {}", roleDTO.getId());
        Role role = roleRepository.findById(roleDTO.getId())
            .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleDTO.getId()));
        if (!role.getRoleName().equals(roleDTO.getRoleName()) && 
            roleRepository.findByRoleName(roleDTO.getRoleName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists: " + roleDTO.getRoleName());
        }
        role.setRoleName(roleDTO.getRoleName());
        Role updatedRole = roleRepository.save(role);
        RoleDTO responseDTO = new RoleDTO();
        responseDTO.setId(updatedRole.getId());
        responseDTO.setRoleName(updatedRole.getRoleName());
        return responseDTO;
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with id {}", id);
        if (!roleRepository.existsById(id)) {
            throw new RoleNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
}