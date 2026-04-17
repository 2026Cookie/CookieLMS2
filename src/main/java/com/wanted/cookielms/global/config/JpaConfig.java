package com.wanted.cookielms.global.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.wanted.cookielms")
@EntityScan(basePackages = "com.wanted.cookielms")
@Configuration
public class JpaConfig {
}
