package in.batur.eksiclone.entryservice.mapper;

import in.batur.eksiclone.entity.entry.Entry;
import in.batur.eksiclone.entity.entry.Tag;
import in.batur.eksiclone.entity.entry.Topic;
import in.batur.eksiclone.entryservice.dto.EntryPreviewDTO;
import in.batur.eksiclone.entryservice.dto.TopicDTO;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TopicMapper {
    
    private static final int RECENT_ENTRIES_LIMIT = 3;

    public TopicDTO toDto(Topic topic) {
        if (topic == null) {
            return null;
        }
        
        return TopicDTO.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .description(topic.getDescription())
                .entryCount(topic.getEntryCount())
                .viewCount(topic.getViewCount())
                .tags(topic.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .recentEntries(getRecentEntries(topic))
                .createdDate(topic.getCreatedDate())
                .lastUpdatedDate(topic.getLastUpdatedDate())
                .build();
    }
    
    private List<EntryPreviewDTO> getRecentEntries(Topic topic) {
        return topic.getEntries().stream()
                .filter(entry -> !entry.isDeleted())
                .sorted(Comparator.comparing(Entry::getCreatedDate).reversed())
                .limit(RECENT_ENTRIES_LIMIT)
                .map(this::convertToEntryPreview)
                .collect(Collectors.toList());
    }
    
    private EntryPreviewDTO convertToEntryPreview(Entry entry) {
        String previewContent = entry.getContent();
        if (previewContent.length() > 100) {
            previewContent = previewContent.substring(0, 97) + "...";
        }
        
        return EntryPreviewDTO.builder()
                .id(entry.getId())
                .content(previewContent)
                .authorId(entry.getAuthor().getId())
                .authorUsername(entry.getAuthor().getUsername())
                .favoriteCount(entry.getFavoriteCount())
                .createdDate(entry.getCreatedDate())
                .build();
    }
}