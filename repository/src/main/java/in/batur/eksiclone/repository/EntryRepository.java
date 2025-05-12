package in.batur.eksiclone.repository;

import in.batur.eksiclone.entity.Entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
    
    Page<Entry> findByTopicId(Long topicId, Pageable pageable);
    
    Page<Entry> findByAuthorId(Long authorId, Pageable pageable);
    
    @Query("SELECT e FROM Entry e WHERE e.topic.id = :topicId ORDER BY e.favoriteCount DESC")
    Page<Entry> findByTopicIdOrderByFavoriteCount(@Param("topicId") Long topicId, Pageable pageable);
    
    @Query("SELECT e FROM Entry e WHERE e.topic.id = :topicId ORDER BY e.createdDate DESC")
    Page<Entry> findByTopicIdOrderByCreatedDateDesc(@Param("topicId") Long topicId, Pageable pageable);
    
    @Query("SELECT e FROM Entry e WHERE LOWER(e.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Entry> searchByContent(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT e FROM Entry e WHERE e.createdDate >= :startDate ORDER BY e.favoriteCount DESC")
    Page<Entry> findPopularEntries(@Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM Entry e WHERE e.topic.id = :topicId")
    Long countByTopicId(@Param("topicId") Long topicId);
}