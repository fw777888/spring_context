package org.example.config;

import org.example.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("classpath:db.properties")
public class SpringConfig {
    @Value("${db.login}")
    private String DB_LOGIN;
    @Value("${db.password}")
    private String DB_PASSWORD;
    @Value("${db.url}")
    private String DB_URL;
    @Value("${db.pool}")
    private String DB_POOL;

    @Bean(initMethod = "initConnectionPool")
    ConnectionManager connectionManager() {
        return new ConnectionManager(DB_LOGIN, DB_PASSWORD, DB_URL, DB_POOL);
    }
}
