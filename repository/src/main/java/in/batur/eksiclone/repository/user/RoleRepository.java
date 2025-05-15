package in.batur.eksiclone.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.user.Role;
import in.batur.eksiclone.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RoleRepository extends JpaRepository<Role, Long>, BaseRepository<Role> {
    
    Optional<Role> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT r FROM Role r WHERE SIZE(r.users) > 0")
    List<Role> findRolesWithUsers();
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.roles r WHERE r.id = :roleId")
    boolean isRoleInUse(Long roleId);
}