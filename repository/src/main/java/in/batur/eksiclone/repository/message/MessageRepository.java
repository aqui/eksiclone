package in.batur.eksiclone.repository.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.message.Message;
import in.batur.eksiclone.entity.user.User;
import in.batur.eksiclone.repository.BaseRepository;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface MessageRepository extends JpaRepository<Message, Long>, BaseRepository<Message> {
    
    Page<Message> findBySenderAndIsDeletedBySenderFalseOrderByCreatedDateDesc(User sender, Pageable pageable);
    
    Page<Message> findByReceiverAndIsDeletedByReceiverFalseOrderByCreatedDateDesc(User receiver, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = :user AND m.isDeletedBySender = false) OR (m.receiver = :user AND m.isDeletedByReceiver = false) ORDER BY m.createdDate DESC")
    Page<Message> findByUserOrderByCreatedDateDesc(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.isRead = false AND m.isDeletedByReceiver = false")
    long countUnreadMessagesByReceiver(@Param("receiver") User receiver);
    
    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :user AND m.isDeletedByReceiver = false")
    List<User> findDistinctSendersByReceiver(@Param("user") User user);
    
    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = :user AND m.isDeletedBySender = false")
    List<User> findDistinctReceiversBySender(@Param("user") User user);
    
    @Query("SELECT DISTINCT CASE WHEN m.sender = :user THEN m.receiver ELSE m.sender END FROM Message m WHERE (m.sender = :user AND m.isDeletedBySender = false) OR (m.receiver = :user AND m.isDeletedByReceiver = false)")
    List<User> findMessageContactsByUser(@Param("user") User user);
}
