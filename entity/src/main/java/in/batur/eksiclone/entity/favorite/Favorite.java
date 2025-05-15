package in.batur.eksiclone.entity.favorite;

import in.batur.eksiclone.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorite")
@Getter
@Setter
public class Favorite extends BaseEntity {

}
