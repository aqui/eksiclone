package in.batur.eksiclone.roleservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.repository.RoleRepository;
import in.batur.eksiclone.repository.UserRepository;
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
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<RoleDTO> findAll() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = roles.stream()
            .map(roleMapper::toDto)
            .collect(Collectors.toList());
        log.info("Found {} roles", roleDTOs.size());
        return roleDTOs;
    }

    @Override
    public RoleDTO findById(Long id) {
        log.info("Fetching role with id {}", id);
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        log.info("Creating role with name {}", roleDTO.getRoleName());
        if (roleRepository.findByRoleName(roleDTO.getRoleName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists: " + roleDTO.getRoleName());
        }
        
        Role role = roleMapper.toEntity(roleDTO);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
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
        
        roleMapper.updateEntity(roleDTO, role);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with id {}", id);
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        
        // Check if any users have this role before deleting
        long userCount = userRepository.countByRoleId(id);
        if (userCount > 0) {
            throw new IllegalStateException("Cannot delete role: " + role.getRoleName() + 
                ". It is assigned to " + userCount + " user(s).");
        }
        
        roleRepository.deleteById(id);
    }
}