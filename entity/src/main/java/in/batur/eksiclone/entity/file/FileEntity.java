package in.batur.eksiclone.entity.file;

import in.batur.eksiclone.entity.BaseEntity;
import in.batur.eksiclone.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
public class FileEntity extends BaseEntity {
    
    @Column(nullable = false)
    private String filename;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "content_type", nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private long size;
    
    @Column(nullable = false)
    private String path;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Column(name = "is_public")
    private boolean isPublic = false;
    
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    
    @Column(name = "download_count")
    private int downloadCount = 0;
    
    public void incrementDownloadCount() {
        this.downloadCount++;
    }
}
