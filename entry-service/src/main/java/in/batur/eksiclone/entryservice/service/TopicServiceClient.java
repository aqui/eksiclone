package in.batur.eksiclone.entryservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "topic-service")
public interface TopicServiceClient {
    
	@PutMapping("/api/v1/topics/{id}/increment-entry")
	void incrementEntryCount(@PathVariable Long id);

	@PutMapping("/api/v1/topics/{id}/decrement-entry")
	void decrementEntryCount(@PathVariable Long id);
}