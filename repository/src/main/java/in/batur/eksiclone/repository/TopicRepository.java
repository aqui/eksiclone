package in.batur.eksiclone.repository;

import in.batur.eksiclone.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByTitleIgnoreCase(String title);
    
    boolean existsByTitleIgnoreCase(String title);
    
    Page<Topic> findByOrderByCreatedDateDesc(Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.trending = true ORDER BY t.entryCount DESC")
    Page<Topic> findTrending(Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.createdDate >= :startDate ORDER BY t.entryCount DESC")
    Page<Topic> findRecentPopular(@Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Topic> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE :tag MEMBER OF t.tags")
    Page<Topic> findByTag(@Param("tag") String tag, Pageable pageable);
}