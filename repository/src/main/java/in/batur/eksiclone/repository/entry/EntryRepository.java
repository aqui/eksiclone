package in.batur.eksiclone.repository.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.entry.Entry;
import in.batur.eksiclone.entity.entry.Topic;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.repository.BaseRepository;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface EntryRepository extends JpaRepository<Entry, Long>, BaseRepository<Entry> {
    
    Page<Entry> findByTopicAndIsDeletedFalseOrderByCreatedDateDesc(Topic topic, Pageable pageable);
    
    Page<Entry> findByAuthorAndIsDeletedFalseOrderByCreatedDateDesc(User author, Pageable pageable);
    
    Page<Entry> findByIsDeletedFalseOrderByCreatedDateDesc(Pageable pageable);
    
    Page<Entry> findByIsDeletedFalseOrderByFavoriteCountDesc(Pageable pageable);
    
    Page<Entry> findByContentContainingIgnoreCaseAndIsDeletedFalse(String query, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT e.* FROM entries e JOIN entry_tags et ON e.id = et.entry_id JOIN tags t ON t.id = et.tag_id WHERE t.name = :tagName AND e.is_deleted = false ORDER BY e.created_date DESC", nativeQuery = true)
    Page<Entry> findByTagNameAndNotDeleted(@Param("tagName") String tagName, Pageable pageable);
    
    @Query(value = "SELECT COUNT(*) FROM entries WHERE topic_id = :topicId AND is_deleted = false", nativeQuery = true)
    long countByTopicAndNotDeleted(@Param("topicId") Long topicId);
    
    List<Entry> findByTopicAndIsDeletedFalse(Topic topic);
}