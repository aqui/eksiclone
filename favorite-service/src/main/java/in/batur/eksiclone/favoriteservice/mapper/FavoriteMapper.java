package in.batur.eksiclone.favoriteservice.mapper;

import in.batur.eksiclone.entity.favorite.Favorite;
import in.batur.eksiclone.favoriteservice.dto.FavoriteDTO;

import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoriteDTO toDto(Favorite favorite) {
        if (favorite == null) {
            return null;
        }
        String contentPreview = favorite.getEntry().getContent();
        if (contentPreview.length() > 100) {
            contentPreview = contentPreview.substring(0, 97) + "...";
        }
        
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .username(favorite.getUser().getUsername())
                .entryId(favorite.getEntry().getId())
                .entryContent(contentPreview)
                .topicId(favorite.getEntry().getTopic().getId())
                .topicTitle(favorite.getEntry().getTopic().getTitle())
                .createdDate(favorite.getCreatedDate())
                .build();
    }
}