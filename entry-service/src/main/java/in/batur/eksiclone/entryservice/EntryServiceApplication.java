package in.batur.eksiclone.entryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EntityScan(basePackages = {"in.batur.eksiclone.entity.entry"}, 
            basePackageClasses = {in.batur.eksiclone.entity.BaseEntity.class, 
                                  in.batur.eksiclone.entity.user.User.class})
@EnableJpaRepositories({"in.batur.eksiclone.repository.entry", 
                        "in.batur.eksiclone.repository.user"})
@ComponentScan({"in.batur.eksiclone.entryservice", 
                "in.batur.eksiclone.repository.entry", 
                "in.batur.eksiclone.repository.user"})
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class EntryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EntryServiceApplication.class, args);
    }
}