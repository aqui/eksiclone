package in.batur.eksiclone.repository.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.file.FileEntity;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.repository.BaseRepository;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FileRepository extends JpaRepository<FileEntity, Long>, BaseRepository<FileEntity> {
    
    Optional<FileEntity> findByFilenameAndIsDeletedFalse(String filename);
    
    Page<FileEntity> findByOwnerAndIsDeletedFalseOrderByCreatedDateDesc(User owner, Pageable pageable);
    
    Page<FileEntity> findByIsPublicTrueAndIsDeletedFalseOrderByCreatedDateDesc(Pageable pageable);
    
    Page<FileEntity> findByContentTypeContainingAndIsDeletedFalseOrderByCreatedDateDesc(String contentType, Pageable pageable);
    
    @Query("SELECT f FROM FileEntity f WHERE f.owner = :owner AND f.contentType LIKE %:contentType% AND f.isDeleted = false ORDER BY f.createdDate DESC")
    Page<FileEntity> findByOwnerAndContentTypeOrderByCreatedDateDesc(@Param("owner") User owner, @Param("contentType") String contentType, Pageable pageable);
    
    @Query(value = "SELECT IFNULL(SUM(f.size), 0) FROM file_entity f WHERE f.owner_id = :ownerId AND f.is_deleted = false", nativeQuery = true)
    Long getTotalSizeByOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT COUNT(f) FROM FileEntity f WHERE f.owner = :owner AND f.isDeleted = false")
    Long countByOwner(@Param("owner") User owner);
}
