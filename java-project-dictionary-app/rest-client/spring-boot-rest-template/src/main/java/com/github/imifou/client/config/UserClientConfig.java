package com.github.imifou.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "rest-template.client.config.user")
public record UserClientConfig(String url, long connectionTimeout, long readTimeout) {
}