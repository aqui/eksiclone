package in.batur.eksiclone.favoriteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"in.batur.eksiclone.entity.favorite", 
                           "in.batur.eksiclone.entity.user",
                           "in.batur.eksiclone.entity.entry"}, 
             basePackageClasses = {in.batur.eksiclone.entity.BaseEntity.class})
@EnableJpaRepositories({"in.batur.eksiclone.repository.favorite", 
                       "in.batur.eksiclone.repository.user",
                       "in.batur.eksiclone.repository.entry"})
@ComponentScan({"in.batur.eksiclone.favoriteservice", 
               "in.batur.eksiclone.repository.favorite",
               "in.batur.eksiclone.repository.user",
               "in.batur.eksiclone.repository.entry"})
public class FavoriteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FavoriteServiceApplication.class, args);
    }
}