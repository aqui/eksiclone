package in.batur.eksiclone.entryservice.mapper;

import in.batur.eksiclone.entity.Entry;
import in.batur.eksiclone.entryservice.dto.EntryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EntryMapper {
    
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicTitle", source = "topic.title")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    EntryDTO toDto(Entry entry);
    
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "version", ignore = true)
    Entry toEntity(EntryDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "favoriteCount", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateEntity(EntryDTO dto, @MappingTarget Entry entry);
}