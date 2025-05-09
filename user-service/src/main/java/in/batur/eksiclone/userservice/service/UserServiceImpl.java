package in.batur.eksiclone.userservice.service;

import in.batur.eksiclone.entity.User;
import in.batur.eksiclone.repository.UserRepository;
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
}