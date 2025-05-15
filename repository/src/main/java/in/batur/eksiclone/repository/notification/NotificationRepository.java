package in.batur.eksiclone.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.notification.Notification;
import in.batur.eksiclone.repository.BaseRepository;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Long>, BaseRepository<Notification>{
	
}