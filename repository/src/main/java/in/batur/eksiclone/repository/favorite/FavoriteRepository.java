package in.batur.eksiclone.repository.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import in.batur.eksiclone.entity.favorite.Favorite;
import in.batur.eksiclone.repository.BaseRepository;

@Repository
@Transactional
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, BaseRepository<Favorite>{
	
}