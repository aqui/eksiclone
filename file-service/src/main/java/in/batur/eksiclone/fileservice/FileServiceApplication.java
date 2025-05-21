package in.batur.eksiclone.fileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"in.batur.eksiclone.entity.file", 
                           "in.batur.eksiclone.entity.user"}, 
             basePackageClasses = {in.batur.eksiclone.entity.BaseEntity.class})
@EnableJpaRepositories({"in.batur.eksiclone.repository.file", 
                       "in.batur.eksiclone.repository.user"})
@ComponentScan({"in.batur.eksiclone.fileservice", 
               "in.batur.eksiclone.repository.file",
               "in.batur.eksiclone.repository.user"})
public class FileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}
