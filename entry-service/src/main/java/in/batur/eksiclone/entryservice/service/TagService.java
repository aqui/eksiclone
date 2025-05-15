package in.batur.eksiclone.entryservice.service;

import in.batur.eksiclone.entryservice.dto.CreateTagRequest;
import in.batur.eksiclone.entryservice.dto.TagDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    TagDTO createTag(CreateTagRequest request);
    
    TagDTO getTag(Long id);
    
    TagDTO getTagByName(String name);
    
    TagDTO updateTag(Long id, CreateTagRequest request);
    
    void deleteTag(Long id);
    
    Page<TagDTO> getAllTags(Pageable pageable);
    
    Page<TagDTO> getPopularTags(Pageable pageable);
    
    List<TagDTO> getTagsByTopicId(Long topicId);
}