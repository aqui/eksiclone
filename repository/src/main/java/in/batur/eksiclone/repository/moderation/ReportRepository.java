package in.batur.eksiclone.repository.moderation;

import in.batur.eksiclone.entity.moderation.Report;
import in.batur.eksiclone.entity.moderation.Report.ReportStatus;
import in.batur.eksiclone.entity.moderation.Report.ReportType;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.repository.BaseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ReportRepository extends JpaRepository<Report, Long>, BaseRepository<Report> {
    
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    
    Page<Report> findByReporter(User reporter, Pageable pageable);
    
    Page<Report> findByReviewer(User reviewer, Pageable pageable);
    
    Page<Report> findByContentTypeAndContentId(ReportType contentType, Long contentId, Pageable pageable);
    
    @Query("SELECT r FROM Report r WHERE r.contentType = :contentType AND r.contentId = :contentId AND r.status = :status")
    List<Report> findByContentTypeAndContentIdAndStatus(
            @Param("contentType") ReportType contentType,
            @Param("contentId") Long contentId,
            @Param("status") ReportStatus status);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    long countByStatus(@Param("status") ReportStatus status);
    
    @Query("SELECT r.contentType, COUNT(r) FROM Report r WHERE r.status = :status GROUP BY r.contentType")
    List<Object[]> countByContentTypeAndStatus(@Param("status") ReportStatus status);
}
