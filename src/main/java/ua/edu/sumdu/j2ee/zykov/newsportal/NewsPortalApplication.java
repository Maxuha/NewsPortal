package ua.edu.sumdu.j2ee.zykov.newsportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "ua.edu.sumdu.j2ee.zykov")
@SpringBootApplication
@EnableCaching
public class NewsPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsPortalApplication.class, args);
    }

}
