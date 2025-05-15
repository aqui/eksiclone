package in.batur.eksiclone.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.message.Message;
import in.batur.eksiclone.repository.BaseRepository;

@Repository
@Transactional
public interface MessageRepository extends JpaRepository<Message, Long>, BaseRepository<Message>{
	
}