package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.entity.User;
import in.batur.eksiclone.repository.RoleRepository;
import in.batur.eksiclone.repository.UserRepository;
import in.batur.eksiclone.userservice.dto.UserDTO;
import in.batur.eksiclone.userservice.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserMessagingService messagingService;

    // Constructor injection
    public UserServiceImpl(
        UserRepository userRepository, 
        RoleRepository roleRepository, 
        UserMapper userMapper, 
        PasswordEncoder passwordEncoder,
        UserMessagingService messagingService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.messagingService = messagingService;
    }

    @Override
    public List<UserDTO> findAll() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
        log.info("Found {} users", userDTOs.size());
        return userDTOs;
    }
    
    @Override
    public UserDTO findById(Long id) {
        log.info("Fetching user with id {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating user with username {}", userDTO.getUsername());
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }
        
        User user = userMapper.toEntity(userDTO);
        
        // Password encoding
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        List<Role> roles = userDTO.getRoleNames().stream()
            .map(roleName -> roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
            .collect(Collectors.toList());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        
        // Send message for user creation
        messagingService.sendUserCreatedEvent(savedUser);
        
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        log.info("Updating user with id {}", userDTO.getId());
        User user = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userDTO.getId()));
        
        // Check if username is being changed and if the new username is taken
        if (!user.getUsername().equals(userDTO.getUsername()) && userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
        }
        // Check if email is being changed and if the new email is taken
        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        userMapper.updateEntity(userDTO, user);
        
        // Handle password separately with encoding
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // Handle roles
        List<Role> roles = userDTO.getRoleNames().stream()
            .map(roleName -> roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
            .collect(Collectors.toList());
        user.setRoles(roles);

        User updatedUser = userRepository.save(user);
        
        // Send message for user update
        messagingService.sendUserUpdatedEvent(updatedUser);
        
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Send message before deleting the user
        messagingService.sendUserDeletedEvent(user);
        
        // Delete the user
        userRepository.delete(user);
        log.info("User deleted successfully: {}", user.getUsername());
    }
}