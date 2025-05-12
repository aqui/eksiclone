package in.batur.eksiclone.entryservice.service;

import in.batur.eksiclone.entryservice.dto.EntryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntryService {
    EntryDTO createEntry(EntryDTO entryDTO);
    
    EntryDTO getEntryById(Long id);
    
    Page<EntryDTO> getEntriesByTopicId(Long topicId, Pageable pageable);
    
    Page<EntryDTO> getEntriesByTopicIdOrderByFavorites(Long topicId, Pageable pageable);
    
    Page<EntryDTO> getEntriesByTopicIdOrderByNewest(Long topicId, Pageable pageable);
    
    Page<EntryDTO> getEntriesByAuthorId(Long authorId, Pageable pageable);
    
    Page<EntryDTO> searchEntries(String keyword, Pageable pageable);
    
    Page<EntryDTO> getPopularEntries(Pageable pageable);
    
    EntryDTO updateEntry(Long id, EntryDTO entryDTO);
    
    void deleteEntry(Long id);
    
    EntryDTO incrementFavoriteCount(Long id);
    
    EntryDTO decrementFavoriteCount(Long id);
    
    Long countEntriesByTopicId(Long topicId);
}