package in.batur.eksiclone.repository.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.entry.Entry;
import in.batur.eksiclone.entity.favorite.Favorite;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, BaseRepository<Favorite> {
    
    Page<Favorite> findByUserOrderByCreatedDateDesc(User user, Pageable pageable);
    
    Page<Favorite> findByEntryOrderByCreatedDateDesc(Entry entry, Pageable pageable);
    
    Optional<Favorite> findByUserAndEntry(User user, Entry entry);
    
    boolean existsByUserAndEntry(User user, Entry entry);
    
    long countByEntry(Entry entry);
    
    @Query(value = "SELECT e.id FROM favorites f JOIN entries e ON f.entry_id = e.id WHERE f.user_id = :userId", nativeQuery = true)
    List<Long> findEntryIdsByUserId(@Param("userId") Long userId);
    
    void deleteByUserAndEntry(User user, Entry entry);
}