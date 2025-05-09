package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.entity.User;
import in.batur.eksiclone.repository.RoleRepository;
import in.batur.eksiclone.repository.UserRepository;
import in.batur.eksiclone.userservice.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<UserDTO> findAll() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setName(user.getName());
            dto.setLastName(user.getLastName());
            dto.setRoleNames(user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
        log.info("Found {} users", userDTOs.size());
        return userDTOs;
    }

    @Override
    public UserDTO findById(Long id) {
        log.info("Fetching user with id {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setRoleNames(user.getRoles().stream()
            .map(role -> role.getRoleName())
            .collect(Collectors.toList()));
        return dto;
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
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());

        List<Role> roles = userDTO.getRoleNames().stream()
            .map(roleName -> roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
            .collect(Collectors.toList());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        
        // Create response DTO without password
        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(savedUser.getId());
        responseDTO.setUsername(savedUser.getUsername());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setName(savedUser.getName());
        responseDTO.setLastName(savedUser.getLastName());
        responseDTO.setRoleNames(savedUser.getRoles().stream()
            .map(role -> role.getRoleName())
            .collect(Collectors.toList()));
        return responseDTO;
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

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(userDTO.getPassword());
        }
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());

        List<Role> roles = userDTO.getRoleNames().stream()
            .map(roleName -> roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
            .collect(Collectors.toList());
        user.setRoles(roles);

        User updatedUser = userRepository.save(user);
        
        // Create response DTO without password
        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(updatedUser.getId());
        responseDTO.setUsername(updatedUser.getUsername());
        responseDTO.setEmail(updatedUser.getEmail());
        responseDTO.setName(updatedUser.getName());
        responseDTO.setLastName(updatedUser.getLastName());
        responseDTO.setRoleNames(updatedUser.getRoles().stream()
            .map(role -> role.getRoleName())
            .collect(Collectors.toList()));
        return responseDTO;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}