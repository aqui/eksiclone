package in.batur.eksiclone.entryservice.mapper;

import in.batur.eksiclone.entity.entry.Tag;
import in.batur.eksiclone.entryservice.dto.TagDTO;

import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagDTO toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        
        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .entryCount(tag.getEntries().size())
                .topicCount(tag.getTopics().size())
                .createdDate(tag.getCreatedDate())
                .lastUpdatedDate(tag.getLastUpdatedDate())
                .build();
    }
}