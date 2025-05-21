package in.batur.eksiclone.messageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"in.batur.eksiclone.entity.message", 
                           "in.batur.eksiclone.entity.user"}, 
             basePackageClasses = {in.batur.eksiclone.entity.BaseEntity.class})
@EnableJpaRepositories({"in.batur.eksiclone.repository.message", 
                       "in.batur.eksiclone.repository.user"})
@ComponentScan({"in.batur.eksiclone.messageservice", 
               "in.batur.eksiclone.repository.message",
               "in.batur.eksiclone.repository.user"})
public class MessageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageServiceApplication.class, args);
    }
}
