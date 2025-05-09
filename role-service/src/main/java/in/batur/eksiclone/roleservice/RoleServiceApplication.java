package in.batur.eksiclone.roleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "in.batur.eksiclone.repository")
@EntityScan(basePackages = "in.batur.eksiclone.entity")
@ComponentScan(basePackages = {
    "in.batur.eksiclone.roleservice",
    "in.batur.eksiclone.security.jwt",
    "in.batur.eksiclone.security.filter",
    "in.batur.eksiclone.security.service",
    "in.batur.eksiclone.security.util",
    "in.batur.eksiclone.security.dto",
    "in.batur.eksiclone.security.config"  // Bu satırı ekleyin
}, excludeFilters = {
    @Filter(type = FilterType.REGEX, pattern = "in\\.batur\\.eksiclone\\.security\\.controller\\..*")
})
public class RoleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoleServiceApplication.class, args);
    }
}