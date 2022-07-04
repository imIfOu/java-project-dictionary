package com.github.imifou.configuration;

import io.r2dbc.spi.ConnectionFactory;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@NoArgsConstructor
public class DataBaseInitializer {

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        var initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ByteArrayResource(loadResource("schema.sql"))));
        return initializer;
    }

    private byte[] loadResource(String resourcePath) {
        try {
            InputStream inputStream = DataBaseInitializer.class.getClassLoader().getResourceAsStream(resourcePath);
            return inputStream.readAllBytes();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
