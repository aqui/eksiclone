package in.batur.eksiclone.moderationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"in.batur.eksiclone.entity.moderation", 
                           "in.batur.eksiclone.entity.user"}, 
             basePackageClasses = {in.batur.eksiclone.entity.BaseEntity.class})
@EnableJpaRepositories({"in.batur.eksiclone.repository.moderation", 
                       "in.batur.eksiclone.repository.user"})
@ComponentScan({"in.batur.eksiclone.moderationservice", 
               "in.batur.eksiclone.repository.moderation",
               "in.batur.eksiclone.repository.user"})
public class ModerationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModerationServiceApplication.class, args);
    }
}
