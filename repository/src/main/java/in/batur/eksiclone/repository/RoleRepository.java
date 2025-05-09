package in.batur.eksiclone.repository;

import in.batur.eksiclone.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long>, BaseRepository<Role> {
    Optional<Role> findByRoleName(String roleName);
}