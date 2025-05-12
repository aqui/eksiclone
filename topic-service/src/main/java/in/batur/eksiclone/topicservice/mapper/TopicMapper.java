package in.batur.eksiclone.topicservice.mapper;

import in.batur.eksiclone.entity.Topic;
import in.batur.eksiclone.topicservice.dto.TopicDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    
    @Mapping(target = "createdBy", source = "createdBy.username")
    @Mapping(target = "createdById", source = "createdBy.id")
    TopicDTO toDto(Topic topic);
    
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    Topic toEntity(TopicDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "entryCount", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateEntity(TopicDTO dto, @MappingTarget Topic topic);
}