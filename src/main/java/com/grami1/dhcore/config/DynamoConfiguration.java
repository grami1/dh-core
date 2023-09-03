package com.grami1.dhcore.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class DynamoConfiguration {

    @Bean
    public DynamoDbClient dynamoDbClient(AwsProperties properties) {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(properties.accessKey(), properties.secretKey());
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);

        return DynamoDbClient.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
