package in.batur.eksiclone.entity.favorite;

import in.batur.eksiclone.entity.BaseEntity;
import in.batur.eksiclone.entity.entry.Entry;
import in.batur.eksiclone.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "entry_id"})
})
@Getter
@Setter
public class Favorite extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id", nullable = false)
    private Entry entry;
}