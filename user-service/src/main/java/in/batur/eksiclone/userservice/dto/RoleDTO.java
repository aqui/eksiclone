package in.batur.eksiclone.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RoleDTO {
    private Long id;

    @NotBlank(message = "Role name is mandatory")
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    @Pattern(regexp = "^ROLE_[A-Z0-9_]+$", message = "Role name must start with 'ROLE_' followed by uppercase letters, numbers, and underscores")
    private String roleName;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}