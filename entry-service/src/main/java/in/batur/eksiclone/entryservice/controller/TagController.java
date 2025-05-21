package in.batur.eksiclone.entryservice.controller;

import in.batur.eksiclone.entryservice.dto.ApiResponse;
import in.batur.eksiclone.entryservice.dto.CreateTagRequest;
import in.batur.eksiclone.entryservice.dto.TagDTO;
import in.batur.eksiclone.entryservice.service.TagService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TagDTO>> createTag(@RequestBody @Validated CreateTagRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(tagService.createTag(request), "Tag created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagDTO>> getTag(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(tagService.getTag(id), "Tag retrieved successfully"));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<TagDTO>> getTagByName(@PathVariable String name) {
        return ResponseEntity.ok(new ApiResponse<>(tagService.getTagByName(name), "Tag retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TagDTO>> updateTag(
            @PathVariable Long id,
            @RequestBody @Validated CreateTagRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(tagService.updateTag(id, request), "Tag updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "Tag deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TagDTO>>> getAllTags(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(tagService.getAllTags(pageable), "Tags retrieved successfully"));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<TagDTO>>> getPopularTags(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(tagService.getPopularTags(pageable), "Popular tags retrieved successfully"));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ApiResponse<List<TagDTO>>> getTagsByTopicId(@PathVariable Long topicId) {
        return ResponseEntity.ok(new ApiResponse<>(tagService.getTagsByTopicId(topicId), "Topic tags retrieved successfully"));
    }
}