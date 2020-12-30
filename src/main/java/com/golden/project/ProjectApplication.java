package com.golden.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
@Configuration
@EnableAsync
@EnableCaching
@EntityScan(basePackages = {"com.golden.project.repositories.entities"})
@EnableJpaRepositories(basePackages = {"com.golden.project.repositories"})
public class ProjectApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate()
    {
        return new RestTemplate();
    }

    @Bean
    public CacheManager cacheManager()
    {
        // configure and return an implementation of Spring's CacheManager SPI
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.getCache("default");
        return cacheManager;
    }

}
