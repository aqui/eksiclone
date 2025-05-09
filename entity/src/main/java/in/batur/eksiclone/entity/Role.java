package in.batur.eksiclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String roleName;

    @Version
    private Long version;
}