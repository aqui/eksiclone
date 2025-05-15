package in.batur.eksiclone.entryservice.service;

import in.batur.eksiclone.entryservice.dto.CreateEntryRequest;
import in.batur.eksiclone.entryservice.dto.EntryDTO;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntryService {
    EntryDTO createEntry(CreateEntryRequest request);
    
    EntryDTO getEntry(Long id);
    
    EntryDTO updateEntry(Long id, String content, Set<String> tags);
    
    void deleteEntry(Long id);
    
    Page<EntryDTO> getEntriesByTopic(Long topicId, Pageable pageable);
    
    Page<EntryDTO> getEntriesByAuthor(Long authorId, Pageable pageable);
    
    Page<EntryDTO> getEntriesByTag(String tagName, Pageable pageable);
    
    Page<EntryDTO> getLatestEntries(Pageable pageable);
    
    Page<EntryDTO> getPopularEntries(Pageable pageable);
    
    Page<EntryDTO> searchEntries(String query, Pageable pageable);
    
    // Favori işlevleri eklendi
    EntryDTO incrementFavoriteCount(Long id);
    
    EntryDTO decrementFavoriteCount(Long id);
}