package in.batur.eksiclone.userservice.service;

import java.util.List;

import in.batur.eksiclone.userservice.dto.UserDTO;

public interface UserService {
    List<UserDTO> findAll();
    UserDTO findById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    void deleteUser(Long id);
}