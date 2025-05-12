package in.batur.eksiclone.topicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "in.batur.eksiclone.repository")
@EntityScan(basePackages = "in.batur.eksiclone.entity")
@EnableAsync
@ComponentScan(basePackages = {
    "in.batur.eksiclone.topicservice",
    "in.batur.eksiclone.shared"
})
public class TopicServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TopicServiceApplication.class, args);
    }
}