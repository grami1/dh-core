package com.grami1.dhcore.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public record WebSecurityProperties(
        String issuerUri,
        String audiences
) {}
