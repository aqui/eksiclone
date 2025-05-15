package in.batur.eksiclone.entryservice.service.impl;

import in.batur.eksiclone.entity.entry.Tag;
import in.batur.eksiclone.entryservice.dto.CreateTagRequest;
import in.batur.eksiclone.entryservice.dto.TagDTO;
import in.batur.eksiclone.entryservice.mapper.TagMapper;
import in.batur.eksiclone.entryservice.service.TagService;
import in.batur.eksiclone.repository.entry.TagRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public TagDTO createTag(CreateTagRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        
        // Check if tag already exists
        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag with this name already exists");
        }
        
        // Create tag
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        
        tag = tagRepository.save(tag);
        
        return tagMapper.toDto(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDTO getTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        
        return tagMapper.toDto(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDTO getTagByName(String name) {
        Tag tag = tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        
        return tagMapper.toDto(tag);
    }

    @Override
    @Transactional
    public TagDTO updateTag(Long id, CreateTagRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        
        // Check if new name is already taken by another tag
        if (!tag.getName().equalsIgnoreCase(request.getName())) {
            if (tagRepository.existsByNameIgnoreCase(request.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag with this name already exists");
            }
        }
        
        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        
        tag = tagRepository.save(tag);
        
        return tagMapper.toDto(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        
        // Check if tag is being used by entries or topics
        if (!tag.getEntries().isEmpty() || !tag.getTopics().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Cannot delete tag as it is used by " + tag.getEntries().size() + " entries and " +
                tag.getTopics().size() + " topics"
            );
        }
        
        tagRepository.delete(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDTO> getPopularTags(Pageable pageable) {
        return tagRepository.findPopularTags(pageable)
                .map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> getTagsByTopicId(Long topicId) {
        return tagRepository.findByTopicId(topicId).stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }
}