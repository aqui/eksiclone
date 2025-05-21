package in.batur.eksiclone.entryservice.mapper;

import in.batur.eksiclone.entity.entry.Entry;
import in.batur.eksiclone.entity.entry.Tag;
import in.batur.eksiclone.entryservice.dto.EntryDTO;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EntryMapper {

    public EntryDTO toDto(Entry entry) {
        if (entry == null) {
            return null;
        }
        
        return EntryDTO.builder()
                .id(entry.getId())
                .content(entry.getContent())
                .topicId(entry.getTopic().getId())
                .topicTitle(entry.getTopic().getTitle())
                .authorId(entry.getAuthorId())
                .authorUsername(entry.getAuthorUsername())
                .favoriteCount(entry.getFavoriteCount())
                .tags(entry.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .createdDate(entry.getCreatedDate())
                .lastUpdatedDate(entry.getLastUpdatedDate())
                .build();
    }
}