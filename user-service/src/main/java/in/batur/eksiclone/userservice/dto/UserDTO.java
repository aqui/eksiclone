package in.batur.eksiclone.userservice.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private String profileImageUrl;
    private boolean isActive;
    private LocalDateTime lastLoginDate;
    private Set<String> roles;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
}