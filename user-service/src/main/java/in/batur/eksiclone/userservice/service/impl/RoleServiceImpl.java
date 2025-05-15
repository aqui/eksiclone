package in.batur.eksiclone.userservice.service.impl;

import in.batur.eksiclone.entity.user.Role;
import in.batur.eksiclone.repository.user.RoleRepository;
import in.batur.eksiclone.userservice.dto.CreateRoleRequest;
import in.batur.eksiclone.userservice.dto.RoleDTO;
import in.batur.eksiclone.userservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.userservice.mapper.RoleMapper;
import in.batur.eksiclone.userservice.service.RoleService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'allRoles:' + #pageable")
    public Page<RoleDTO> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'role:' + #id")
    public RoleDTO getRoleById(Long id) {
        Role role = findRoleById(id);
        return roleMapper.toDto(role);
    }

    private Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "roles", allEntries = true)
    public RoleDTO createRole(CreateRoleRequest request) {
        validateCreateRoleRequest(request);
        
        // Check if role name already exists
        if (roleRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role with this name already exists");
        }
        
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        
        role = roleRepository.save(role);
        
        return roleMapper.toDto(role);
    }
    
    private void validateCreateRoleRequest(CreateRoleRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name is required");
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "roles", key = "'role:' + #id")
    public RoleDTO updateRole(Long id, CreateRoleRequest request) {
        validateCreateRoleRequest(request);
        
        Role role = findRoleById(id);
        
        // Check if new name is already taken by another role
        if (!role.getName().equalsIgnoreCase(request.getName())) {
            if (roleRepository.existsByNameIgnoreCase(request.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role with this name already exists");
            }
        }
        
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        
        role = roleRepository.save(role);
        
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "roles", allEntries = true)
    public void deleteRole(Long id) {
        Role role = findRoleById(id);
        
        // Check if role is assigned to any user - using repository method
        if (roleRepository.isRoleInUse(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Cannot delete role as it is assigned to one or more users"
            );
        }
        
        roleRepository.delete(role);
    }
}