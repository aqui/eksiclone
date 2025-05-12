package in.batur.eksiclone.topicservice.controller;

import in.batur.eksiclone.topicservice.dto.TopicDTO;
import in.batur.eksiclone.topicservice.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/topics")
@Tag(name = "Topic API", description = "APIs for managing topics in Eksiclone")
public class TopicController {
    
    private final TopicService topicService;
    
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }
    
    @Operation(summary = "Create a new topic")
    @ApiResponse(responseCode = "201", description = "Topic created successfully")
    @PostMapping
    public ResponseEntity<TopicDTO> createTopic(
            @Valid @RequestBody TopicDTO topicDTO,
            @RequestParam Long userId) {
        TopicDTO createdTopic = topicService.createTopic(topicDTO, userId);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get a topic by ID")
    @ApiResponse(responseCode = "200", description = "Topic found")
    @GetMapping("/{id}")
    public ResponseEntity<TopicDTO> getTopicById(@PathVariable Long id) {
        TopicDTO topicDTO = topicService.getTopicById(id);
        return ResponseEntity.ok(topicDTO);
    }
    
    @Operation(summary = "Get a topic by title")
    @ApiResponse(responseCode = "200", description = "Topic found")
    @GetMapping("/by-title")
    public ResponseEntity<TopicDTO> getTopicByTitle(@RequestParam String title) {
        TopicDTO topicDTO = topicService.getTopicByTitle(title);
        return ResponseEntity.ok(topicDTO);
    }
    
    @Operation(summary = "Get all topics")
    @GetMapping
    public ResponseEntity<Page<TopicDTO>> getAllTopics(
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable) {
        Page<TopicDTO> topics = topicService.getAllTopics(pageable);
        return ResponseEntity.ok(topics);
    }
    
    @Operation(summary = "Get trending topics")
    @GetMapping("/trending")
    public ResponseEntity<Page<TopicDTO>> getTrendingTopics(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TopicDTO> topics = topicService.getTrendingTopics(pageable);
        return ResponseEntity.ok(topics);
    }
    
    @Operation(summary = "Get recent popular topics")
    @GetMapping("/recent-popular")
    public ResponseEntity<Page<TopicDTO>> getRecentPopularTopics(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TopicDTO> topics = topicService.getRecentPopularTopics(pageable);
        return ResponseEntity.ok(topics);
    }
    
    @Operation(summary = "Search topics by keyword")
    @GetMapping("/search")
    public ResponseEntity<Page<TopicDTO>> searchTopics(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TopicDTO> topics = topicService.searchTopics(keyword, pageable);
        return ResponseEntity.ok(topics);
    }
    
    @Operation(summary = "Update a topic")
    @ApiResponse(responseCode = "200", description = "Topic updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<TopicDTO> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicDTO topicDTO) {
        TopicDTO updatedTopic = topicService.updateTopic(id, topicDTO);
        return ResponseEntity.ok(updatedTopic);
    }
    
    @Operation(summary = "Delete a topic")
    @ApiResponse(responseCode = "204", description = "Topic deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Increment view count")
    @PutMapping("/{id}/increment-view")
    public ResponseEntity<TopicDTO> incrementViewCount(@PathVariable Long id) {
        TopicDTO topicDTO = topicService.incrementViewCount(id);
        return ResponseEntity.ok(topicDTO);
    }
    
    @Operation(summary = "Get topics by tag")
    @GetMapping("/by-tag")
    public ResponseEntity<Page<TopicDTO>> getTopicsByTag(
            @RequestParam String tag,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TopicDTO> topics = topicService.getTopicsByTag(tag, pageable);
        return ResponseEntity.ok(topics);
    }
    
    @Operation(summary = "Add tags to a topic")
    @PutMapping("/{id}/add-tags")
    public ResponseEntity<TopicDTO> addTags(
            @PathVariable Long id,
            @RequestBody Set<String> tags) {
        TopicDTO topicDTO = topicService.addTags(id, tags);
        return ResponseEntity.ok(topicDTO);
    }
    
    @Operation(summary = "Remove tags from a topic")
    @PutMapping("/{id}/remove-tags")
    public ResponseEntity<TopicDTO> removeTags(
            @PathVariable Long id,
            @RequestBody Set<String> tags) {
        TopicDTO topicDTO = topicService.removeTags(id, tags);
        return ResponseEntity.ok(topicDTO);
    }
}