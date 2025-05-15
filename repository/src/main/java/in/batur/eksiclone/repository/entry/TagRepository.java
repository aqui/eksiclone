package in.batur.eksiclone.repository.entry;

import in.batur.eksiclone.entity.entry.Tag;
import in.batur.eksiclone.repository.BaseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long>, BaseRepository<Tag> {
    
    Optional<Tag> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT t FROM Tag t LEFT JOIN t.entries e GROUP BY t.id ORDER BY COUNT(e) DESC")
    Page<Tag> findPopularTags(Pageable pageable);
    
    @Query("SELECT t FROM Tag t WHERE SIZE(t.entries) > 0")
    List<Tag> findTagsWithEntries();
    
    Set<Tag> findByNameIn(Set<String> tagNames);
    
    @Query("SELECT t FROM Tag t JOIN t.topics topic WHERE topic.id = :topicId")
    Set<Tag> findByTopicId(Long topicId);
}