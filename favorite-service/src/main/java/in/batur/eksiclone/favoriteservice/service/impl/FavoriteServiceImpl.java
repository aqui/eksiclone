package in.batur.eksiclone.favoriteservice.service.impl;

import in.batur.eksiclone.entity.entry.Entry;
import in.batur.eksiclone.entity.favorite.Favorite;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.favoriteservice.dto.CreateFavoriteRequest;
import in.batur.eksiclone.favoriteservice.dto.FavoriteDTO;
import in.batur.eksiclone.favoriteservice.exception.ResourceNotFoundException;
import in.batur.eksiclone.favoriteservice.mapper.FavoriteMapper;
import in.batur.eksiclone.favoriteservice.service.FavoriteService;
import in.batur.eksiclone.repository.entry.EntryRepository;
import in.batur.eksiclone.repository.favorite.FavoriteRepository;
import in.batur.eksiclone.repository.user.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final EntryRepository entryRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(
            FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            EntryRepository entryRepository,
            FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.entryRepository = entryRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"favorites", "entries"}, allEntries = true)
    public FavoriteDTO addFavorite(CreateFavoriteRequest request) {
        User user = findUserById(request.getUserId());
        Entry entry = findEntryById(request.getEntryId());
        
        // Check if already favorited
        if (favoriteRepository.existsByUserAndEntry(user, entry)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry already in favorites");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setEntry(entry);
        
        favorite = favoriteRepository.save(favorite);
        
        // Increment favorite count in the entry
        entry.incrementFavoriteCount();
        entryRepository.save(entry);
        
        return favoriteMapper.toDto(favorite);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Entry findEntryById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found with id: " + id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"favorites", "entries"}, allEntries = true)
    public void removeFavorite(Long userId, Long entryId) {
        User user = findUserById(userId);
        Entry entry = findEntryById(entryId);
        
        // Find and check if favorite exists
        Favorite favorite = favoriteRepository.findByUserAndEntry(user, entry)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
        
        // Delete the favorite
        favoriteRepository.delete(favorite);
        
        // Decrement favorite count in the entry
        entry.decrementFavoriteCount();
        entryRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long entryId) {
        User user = findUserById(userId);
        Entry entry = findEntryById(entryId);
        return favoriteRepository.existsByUserAndEntry(user, entry);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "favorites", key = "'user:' + #userId + ':' + #pageable")
    public Page<FavoriteDTO> getUserFavorites(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        return favoriteRepository.findByUserOrderByCreatedDateDesc(user, pageable)
                .map(favoriteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "favorites", key = "'entry:' + #entryId + ':' + #pageable")
    public Page<FavoriteDTO> getEntryFavorites(Long entryId, Pageable pageable) {
        Entry entry = findEntryById(entryId);
        return favoriteRepository.findByEntryOrderByCreatedDateDesc(entry, pageable)
                .map(favoriteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "favorites", key = "'count:' + #entryId")
    public long countEntryFavorites(Long entryId) {
        Entry entry = findEntryById(entryId);
        return favoriteRepository.countByEntry(entry);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "favorites", key = "'userEntryIds:' + #userId")
    public List<Long> getUserFavoriteEntryIds(Long userId) {
        // Check if user exists
        findUserById(userId);
        return favoriteRepository.findEntryIdsByUserId(userId);
    }
}