package in.batur.eksiclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>, BaseRepository<User> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}