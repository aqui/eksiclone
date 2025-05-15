package in.batur.eksiclone.repository.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.entry.Topic;
import in.batur.eksiclone.repository.BaseRepository;

import java.util.Optional;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface TopicRepository extends JpaRepository<Topic, Long>, BaseRepository<Topic> {
    
    Optional<Topic> findByTitleIgnoreCase(String title);
    
    boolean existsByTitleIgnoreCase(String title);
    
    Page<Topic> findByIsDeletedFalseOrderByCreatedDateDesc(Pageable pageable);
    
    Page<Topic> findByIsDeletedFalseOrderByEntryCountDesc(Pageable pageable);
    
    Page<Topic> findByIsDeletedFalseOrderByViewCountDesc(Pageable pageable);
    
    Page<Topic> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String query, Pageable pageable);
    
    @Query("SELECT t FROM Topic t JOIN t.tags tag WHERE tag.name = :tagName AND t.isDeleted = false")
    Page<Topic> findByTagNameAndNotDeleted(@Param("tagName") String tagName, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.entryCount > 0 AND t.isDeleted = false ORDER BY t.lastUpdatedDate DESC")
    Page<Topic> findActiveTopicsOrderByLastUpdated(Pageable pageable);
    
    List<Topic> findByIsDeletedFalseAndEntryCountGreaterThan(int entryCount);
}