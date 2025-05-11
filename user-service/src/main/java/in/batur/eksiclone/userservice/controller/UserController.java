package in.batur.eksiclone.userservice.controller;

import in.batur.eksiclone.userservice.dto.UserDTO;
import in.batur.eksiclone.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "APIs for managing users in Eksiclone")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Returns a list of all users with their details excluding passwords")
    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @Operation(summary = "Get a user by ID", description = "Returns the details of a user with the specified ID excluding password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Create a new user", 
               description = "Creates a new user with the provided details. " +
                             "Username and email must be unique. " +
                             "The roles must already exist in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data, duplicate username/email, or role not found")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a user", 
               description = "Updates the details of a user with the specified ID. " +
                             "Username and email must remain unique. " +
                             "The password will only be updated if provided in the request. " +
                             "The roles must already exist in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid user data, duplicate username/email, or role not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        UserDTO updatedUser = userService.updateUser(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user", description = "Permanently deletes a user with the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Hata loglaması
            System.out.println("User deletion error: " + e.getMessage());
            e.printStackTrace();
            // Exception Handler tarafından yakalanacak şekilde hatayı yeniden fırlat
            throw e;
        }
    }
}