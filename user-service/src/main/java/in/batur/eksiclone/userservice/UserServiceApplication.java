package in.batur.eksiclone.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "in.batur.eksiclone.repository")
@EntityScan(basePackages = "in.batur.eksiclone.entity")
@EnableAsync
@ComponentScan(basePackages = {
    "in.batur.eksiclone.userservice",  // Tüm User Service ve taşınan Role Service sınıfları
    "in.batur.eksiclone.security.jwt", 
    "in.batur.eksiclone.security.filter", 
    "in.batur.eksiclone.security.service", 
    "in.batur.eksiclone.security.util", 
    "in.batur.eksiclone.security.dto", 
    "in.batur.eksiclone.security.controller" 
}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
        classes = {
            in.batur.eksiclone.security.config.SecurityConfig.class,
            in.batur.eksiclone.security.controller.JwkSetController.class
        }
    )
})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}