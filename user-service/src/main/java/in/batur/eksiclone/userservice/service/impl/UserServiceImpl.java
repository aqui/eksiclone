package in.batur.eksiclone.userservice.service.impl;

import in.batur.eksiclone.entity.user.Role;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.repository.user.RoleRepository;
import in.batur.eksiclone.repository.user.UserRepository;
import in.batur.eksiclone.userservice.dto.CreateUserRequest;
import in.batur.eksiclone.userservice.dto.UpdateUserRequest;
import in.batur.eksiclone.userservice.dto.UserDTO;
import in.batur.eksiclone.userservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.userservice.mapper.UserMapper;
import in.batur.eksiclone.userservice.service.UserService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    
    public UserServiceImpl(
            UserRepository userRepository, 
            RoleRepository roleRepository, 
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'allUsers:' + #pageable")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'user:' + #id")
    public UserDTO getUserById(Long id) {
        User user = findUserById(id);
        return userMapper.toDto(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'username:' + #username")
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.toDto(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'email:' + #email")
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);
        checkUsernameAndEmailAvailability(request.getUsername(), request.getEmail());
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // In real app, password should be encoded
        user.setEmail(request.getEmail());
        user.setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername());
        user.setBio(request.getBio());
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setActive(true);
        
        // Handle roles if present
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            addRolesToUser(user, request.getRoles());
        }
        
        user = userRepository.save(user);
        
        return userMapper.toDto(user);
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
    }

    private void checkUsernameAndEmailAvailability(String username, String email) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
    }

    private void addRolesToUser(User user, Set<String> roleNames) {
        for (String roleName : roleNames) {
            Role role = roleRepository.findByNameIgnoreCase(roleName)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(roleName);
                        return roleRepository.save(newRole);
                    });
            user.addRole(role);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "users", key = "'user:' + #id")
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);
        
        if (request.getUsername() != null) {
            validateUsernameUpdate(user, request.getUsername());
            user.setUsername(request.getUsername());
        }
        
        if (request.getEmail() != null) {
            validateEmailUpdate(user, request.getEmail());
            user.setEmail(request.getEmail());
        }
        
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
        
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }
        
        // Handle roles if present
        if (request.getRoles() != null) {
            // Clear existing roles and add new ones
            user.getRoles().clear();
            addRolesToUser(user, request.getRoles());
        }
        
        user = userRepository.save(user);
        
        return userMapper.toDto(user);
    }

    private void validateUsernameUpdate(User user, String newUsername) {
        // Check if new username is already taken by another user
        if (!user.getUsername().equals(newUsername) && 
            userRepository.existsByUsername(newUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
    }

    private void validateEmailUpdate(User user, String newEmail) {
        // Check if new email is already taken by another user
        if (!user.getEmail().equals(newEmail) && 
            userRepository.existsByEmail(newEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }
    
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "users", key = "'user:' + #userId")
    public UserDTO assignRoleToUser(Long userId, Long roleId) {
        User user = findUserById(userId);
        Role role = findRoleById(roleId);
        
        user.addRole(role);
        user = userRepository.save(user);
        
        return userMapper.toDto(user);
    }

    private Role findRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "users", key = "'user:' + #userId")
    public UserDTO removeRoleFromUser(Long userId, Long roleId) {
        User user = findUserById(userId);
        Role role = findRoleById(roleId);
        
        user.removeRole(role);
        user = userRepository.save(user);
        
        return userMapper.toDto(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String query, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(query, pageable)
                .map(userMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsersByEmail(String email, Pageable pageable) {
        return userRepository.findByEmailContainingIgnoreCase(email, pageable)
                .map(userMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByRoleName(String roleName, Pageable pageable) {
        return userRepository.findByRoleName(roleName, pageable)
                .map(userMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByRoleId(Long roleId, Pageable pageable) {
        return userRepository.findByRoleId(roleId, pageable)
                .map(userMapper::toDto);
    }
}