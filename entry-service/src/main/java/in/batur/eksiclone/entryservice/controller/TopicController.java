package in.batur.eksiclone.entryservice.controller;

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
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseEntity<TopicDTO> createTopic(@RequestBody @Validated CreateTopicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.createTopic(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicDTO> getTopic(@PathVariable Long id) {
        topicService.incrementViewCount(id);
        return ResponseEntity.ok(topicService.getTopic(id));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<TopicDTO> getTopicByTitle(@PathVariable String title) {
        TopicDTO topic = topicService.getTopicByTitle(title);
        topicService.incrementViewCount(topic.getId());
        return ResponseEntity.ok(topic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicDTO> updateTopic(
            @PathVariable Long id,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Set<String> tags) {
        return ResponseEntity.ok(topicService.updateTopic(id, description, tags));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<Page<TopicDTO>> getLatestTopics(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(topicService.getLatestTopics(pageable));
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<TopicDTO>> getPopularTopics(
            @PageableDefault(size = 20, sort = "entryCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(topicService.getPopularTopics(pageable));
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<Page<TopicDTO>> getMostViewedTopics(
            @PageableDefault(size = 20, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(topicService.getMostViewedTopics(pageable));
    }
    
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<Page<TopicDTO>> getTopicsByTag(
            @PathVariable String tagName,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(topicService.getTopicsByTag(tagName, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TopicDTO>> searchTopics(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(topicService.searchTopics(query, pageable));
    }
}