package in.batur.eksiclone.userservice.controller;

import in.batur.eksiclone.userservice.dto.ApiResponse;
import in.batur.eksiclone.userservice.dto.CreateUserRequest;
import in.batur.eksiclone.userservice.dto.UpdateUserRequest;
import in.batur.eksiclone.userservice.dto.UserDTO;
import in.batur.eksiclone.userservice.service.UserService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(users, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(userService.getUserById(id), "User retrieved successfully"));
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(new ApiResponse<>(userService.getUserByUsername(username), "User retrieved successfully"));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(new ApiResponse<>(userService.getUserByEmail(email), "User retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(createdUser, "User created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id, 
            @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(userService.updateUser(id, request), "User updated successfully"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> partialUpdateUser(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> updates) {
        // Create UpdateUserRequest from Map
        UpdateUserRequest request = new UpdateUserRequest();
        
        if (updates.containsKey("username")) {
            request.setUsername((String) updates.get("username"));
        }
        if (updates.containsKey("email")) {
            request.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("displayName")) {
            request.setDisplayName((String) updates.get("displayName"));
        }
        if (updates.containsKey("bio")) {
            request.setBio((String) updates.get("bio"));
        }
        if (updates.containsKey("profileImageUrl")) {
            request.setProfileImageUrl((String) updates.get("profileImageUrl"));
        }
        if (updates.containsKey("isActive")) {
            request.setIsActive((Boolean) updates.get("isActive"));
        }
        
        return ResponseEntity.ok(new ApiResponse<>(userService.updateUser(id, request), "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "User deleted successfully"));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(new ApiResponse<>(
                userService.assignRoleToUser(userId, roleId), 
                "Role assigned to user successfully"));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<UserDTO>> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(new ApiResponse<>(
                userService.removeRoleFromUser(userId, roleId), 
                "Role removed from user successfully"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> searchUsers(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userService.searchUsers(query, pageable), 
                "Users retrieved successfully"));
    }
    
    @GetMapping("/search/email")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> searchUsersByEmail(
            @RequestParam String email,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userService.searchUsersByEmail(email, pageable), 
                "Users retrieved successfully"));
    }
    
    @GetMapping("/role-name/{roleName}")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsersByRoleName(
            @PathVariable String roleName,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userService.getUsersByRoleName(roleName, pageable),
                "Users with role retrieved successfully"));
    }
    
    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsersByRoleId(
            @PathVariable Long roleId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(
                userService.getUsersByRoleId(roleId, pageable),
                "Users with role retrieved successfully"));
    }
}