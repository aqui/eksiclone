package in.batur.eksiclone.userservice.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String displayName;
    private String bio;
    private String profileImageUrl;
    private Boolean isActive;
    private Set<String> roles;
}