package in.batur.eksiclone.userservice.controller;

import in.batur.eksiclone.userservice.service.UserDTO;
import in.batur.eksiclone.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "APIs for managing users in Eksiclone")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Returns a list of all users with their details")
    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }
}