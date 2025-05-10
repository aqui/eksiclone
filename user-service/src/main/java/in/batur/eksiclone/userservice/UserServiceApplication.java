package in.batur.eksiclone.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "in.batur.eksiclone.repository")
@EntityScan(basePackages = "in.batur.eksiclone.entity")
@ComponentScan(basePackages = {
    "in.batur.eksiclone.userservice",  // User service sınıfları
    "in.batur.eksiclone.security.jwt", // JWT servisi
    "in.batur.eksiclone.security.filter", // Filtreler
    "in.batur.eksiclone.security.service", // UserDetailsService
    "in.batur.eksiclone.security.util", // JWT util sınıfları
    "in.batur.eksiclone.security.dto", // DTO'lar
    "in.batur.eksiclone.security.controller" // Auth controller
}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
        classes = {
            in.batur.eksiclone.security.config.SecurityConfig.class, // Kendi security config sınıfımızı kullanacağız
            in.batur.eksiclone.security.controller.JwkSetController.class // Endpoint çakışmasını önlemek için
        }
    )
})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}