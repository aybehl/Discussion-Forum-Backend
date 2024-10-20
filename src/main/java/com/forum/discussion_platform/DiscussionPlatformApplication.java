package com.forum.discussion_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DiscussionPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscussionPlatformApplication.class, args);
	}

}
