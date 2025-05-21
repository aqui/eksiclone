package in.batur.eksiclone.entryservice.controller;

import in.batur.eksiclone.entryservice.dto.ApiResponse;
import in.batur.eksiclone.entryservice.dto.CreateTopicRequest;
import in.batur.eksiclone.entryservice.dto.TopicDTO;
import in.batur.eksiclone.entryservice.service.TopicService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TopicDTO>> createTopic(@RequestBody @Validated CreateTopicRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(topicService.createTopic(request), "Topic created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TopicDTO>> getTopic(@PathVariable Long id) {
        topicService.incrementViewCount(id);
        return ResponseEntity.ok(new ApiResponse<>(topicService.getTopic(id), "Topic retrieved successfully"));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<ApiResponse<TopicDTO>> getTopicByTitle(@PathVariable String title) {
        TopicDTO topic = topicService.getTopicByTitle(title);
        topicService.incrementViewCount(topic.getId());
        return ResponseEntity.ok(new ApiResponse<>(topic, "Topic retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TopicDTO>> updateTopic(
            @PathVariable Long id,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Set<String> tags) {
        return ResponseEntity.ok(new ApiResponse<>(topicService.updateTopic(id, description, tags), "Topic updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "Topic deleted successfully"));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Page<TopicDTO>>> getLatestTopics(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(topicService.getLatestTopics(pageable), "Latest topics retrieved successfully"));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<TopicDTO>>> getPopularTopics(
            @PageableDefault(size = 20, sort = "entryCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(topicService.getPopularTopics(pageable), "Popular topics retrieved successfully"));
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<ApiResponse<Page<TopicDTO>>> getMostViewedTopics(
            @PageableDefault(size = 20, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(topicService.getMostViewedTopics(pageable), "Most viewed topics retrieved successfully"));
    }
    
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<ApiResponse<Page<TopicDTO>>> getTopicsByTag(
            @PathVariable String tagName,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(topicService.getTopicsByTag(tagName, pageable), "Topics by tag retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<TopicDTO>>> searchTopics(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(topicService.searchTopics(query, pageable), "Search results retrieved successfully"));
    }
}